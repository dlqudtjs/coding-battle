package com.dlqudtjs.codingbattle.service.judge;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.constant.code.GameSuccessCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import com.dlqudtjs.codingbattle.service.game.GameService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.submit.SubmitService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Binds;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    @Value("${docker.secret.key}")
    private String secretKey;

    @Value("${host.testcase.path}")
    private String hostTestcasePath;

    @Value("${bind.testcase.path}")
    private String bindTestcasePath;

    @Value("${host.script.path}")
    private String hostScriptPath;

    @Value("${host.user.code.path}")
    private String hostUserCodePath;

    @Value("${bind.user.code.path}")
    private String bindUserCodePath;

    @Value("${bind.script.path}")
    private String bindScriptPath;

    @Value("${docker.out.directory}")
    private String dockerOutDirectory;

    private final DockerClient dockerClient;
    private final RoomService roomService;
    private final GameService gameService;
    private final SubmitService submitService;

    @Override
    public ResponseDto judgeProblem(JudgeProblemRequestDto judgeProblemRequestDto) {
        validateJudgeProblemRequestDto(judgeProblemRequestDto);
        // 제출 저장
        Submit submit = submitService.savedSubmit(judgeProblemRequestDto);

        String uuid = UUID.randomUUID().toString();
        ProgrammingLanguage submitLanguage =
                ProgrammingLanguage.valueOf(judgeProblemRequestDto.getLanguage().toUpperCase());
        String dockerImageName = submitLanguage.getDockerImageName();
        String createTestcasePath = createHostTestCasePath(judgeProblemRequestDto.getProblemId());
        String createScriptPath = createHostScriptPath(submitLanguage);

        try {
            // 사용자가 제출한 코드를 저장할 디렉토리 생성
            String createUserCodePath = createHostUserCodePath(uuid);

            // 사용자가 제출한 코드를 파일로 생성
            createUserCodeFile(
                    createUserCodePath,
                    judgeProblemRequestDto.getCode(),
                    submitLanguage
            );

            Map<String, String> hostAndBindDirectory = Map.of(
                    createTestcasePath, bindTestcasePath,
                    createUserCodePath, bindUserCodePath,
                    createScriptPath, bindScriptPath
            );

            // 컨테이너 생성
            CreateContainerResponse container = createContainer(dockerImageName, hostAndBindDirectory);
            String containerId = container.getId();

            // 컨테이너 시작
            dockerClient.startContainerCmd(containerId).exec();

            // 컨테이너 내 스크립트 실행
            ExecCreateCmdResponse execCreateCmd = execCreateCmd(containerId, judgeProblemRequestDto, submit);
            String execId = execCreateCmd.getId();

            // ResultCallback.Adapter 사용하여 결과 처리
            dockerClient.execStartCmd(execId)
                    .exec(new ResultCallbackTemplate<>() {
                        @Override
                        public void onComplete() {
                            super.onComplete();
                            // Handle onComplete event
                        }

                        @Override
                        public void onNext(Frame object) {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            // Handle onError event
                        }
                    })
                    .awaitCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseDto.builder()
                .message(GameSuccessCode.GAME_END_SUCCESS.getMessage())
                .status(GameSuccessCode.GAME_END_SUCCESS.getStatus())
                .build();
    }

    @Override
    public void closeDockerContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }

    private CreateContainerResponse createContainer(String dockerImageName, Map<String, String> hostAndBindDirectory) {
        return dockerClient.createContainerCmd(dockerImageName)
                .withHostConfig(createHostConfig(hostAndBindDirectory))
                .withTty(true)
                .exec();
    }

    private ExecCreateCmdResponse execCreateCmd(String containerId,
                                                JudgeProblemRequestDto judgeProblemRequestDto,
                                                Submit submit) {
        return dockerClient.execCreateCmd(containerId)
                // run.sh (roomId, userId, problemId, secretKey)
                .withCmd("sh", "-c", "mkdir " + dockerOutDirectory +
                        "&& bash /script/run.sh " +
                        judgeProblemRequestDto.getRoomId() + " " +
                        judgeProblemRequestDto.getUserId() + " " +
                        judgeProblemRequestDto.getProblemId() + " " +
                        submit.getId() + " " +
                        secretKey)
                .exec();
    }

    private String createHostScriptPath(ProgrammingLanguage language) {
        return hostScriptPath + language.getLanguageName();
    }

    private String createHostTestCasePath(Long problemId) {
        return hostTestcasePath + problemId;
    }

    private String getUserCodePath(String directoryPath, ProgrammingLanguage language) {
        return directoryPath + language.getFileName();
    }

    private String createHostUserCodePath(String uuid) {
        String path = hostUserCodePath + uuid;

        File directory = new File(path);
        directory.mkdirs();

        return path + "/";
    }

    /*
     * 사용자가 제출한 코드를 파일로 생성
     * @param code 사용자가 제출한 코드
     * @param language 사용자가 제출한 코드의 언어
     */
    private void createUserCodeFile(
            String directoryPath,
            String code,
            ProgrammingLanguage language) throws IOException {
        try {
            File file = new File(getUserCodePath(directoryPath, language));
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(code);
            fileWriter.flush();

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HostConfig createHostConfig(Map<String, String> hostAndBindDirectory) {
        Bind[] binds = hostAndBindDirectory.entrySet().stream()
                .map(entry -> new Bind(entry.getKey(), new Volume(entry.getValue())))
                .toArray(Bind[]::new);

        return new HostConfig().withBinds(new Binds(binds));
    }

    private void validateJudgeProblemRequestDto(JudgeProblemRequestDto judgeProblemRequestDto) {
        // 언어 검증
        if (ProgrammingLanguage.isNotContains(judgeProblemRequestDto.getLanguage())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 방이 존재하는지 검증
        if (!roomService.isExistRoom(judgeProblemRequestDto.getRoomId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 방이 시작되었는지 검증
        if (!roomService.isStartedGame(judgeProblemRequestDto.getRoomId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 사용자가 연결되어 있는지 검증
        if (WebsocketSessionHolder.isNotConnected(judgeProblemRequestDto.getUserId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 사용자가 방에 들어가 있는지 검증
        if (!roomService.isExistUserInRoom(judgeProblemRequestDto.getRoomId(), judgeProblemRequestDto.getUserId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 문제가 존재하는지 검증
        if (gameService.getProblemInfoList(judgeProblemRequestDto.getRoomId()).stream()
                .noneMatch(problemInfo -> problemInfo.getProblem().getId()
                        .equals(judgeProblemRequestDto.getProblemId()))) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }
}

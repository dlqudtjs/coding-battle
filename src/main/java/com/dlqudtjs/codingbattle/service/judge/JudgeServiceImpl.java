package com.dlqudtjs.codingbattle.service.judge;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.*;
import static com.dlqudtjs.codingbattle.common.constant.code.SocketConfigCode.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguageManager;
import com.dlqudtjs.codingbattle.common.constant.code.GameConfigCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.game.GameService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.submit.SubmitService;
import com.dlqudtjs.codingbattle.service.user.UserService;
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
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import lombok.RequiredArgsConstructor;

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

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	@Value("${gcs.code.path}")
	private String gcsCodePath;

	private final DockerClient dockerClient;
	private final RoomService roomService;
	private final GameService gameService;
	private final UserService userService;
	private final SubmitService submitService;
	private final Storage storage;

	@Override
	public ResponseDto judgeProblem(
		JudgeProblemRequestDto judgeProblemRequestDto) {
		validateJudgeProblemRequestDto(judgeProblemRequestDto);
		// 제출 저장
		Submit submit = submitService.savedSubmit(judgeProblemRequestDto);

		String uuid = UUID.randomUUID().toString();
		ProgrammingLanguage submitLanguage = judgeProblemRequestDto.getProgrammingLanguage();

		String dockerImageName = ProgrammingLanguageManager.getDockerImageName(
			submitLanguage);
		String createUserCodePath = createHostUserCodePath(uuid,
			submitLanguage);
		String createScriptPath = createHostScriptPath(submitLanguage);
		String createTestcasePath = createHostTestCasePath(
			judgeProblemRequestDto.getProblemId());

		try {
			// 사용자가 제출한 코드를 파일로 생성
			createUserCodeFile(
				createUserCodePath,
				judgeProblemRequestDto.getCode()
			);

			Map<String, String> hostAndBindDirectory = Map.of(
				createTestcasePath, bindTestcasePath,
				hostUserCodePath + uuid, bindUserCodePath,
				createScriptPath, bindScriptPath
			);

			// 컨테이너 생성
			CreateContainerResponse container = createContainer(dockerImageName,
				hostAndBindDirectory);
			String containerId = container.getId();

			// 컨테이너 시작
			dockerClient.startContainerCmd(containerId).exec();

			// 컨테이너 내 스크립트 실행
			ExecCreateCmdResponse execCreateCmd = execCreateCmd(containerId,
				judgeProblemRequestDto, submit);
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
			.message(GameConfigCode.GAME_END_SUCCESS.getMessage())
			.status(GameConfigCode.GAME_END_SUCCESS.getStatus().value())
			.build();
	}

	@Override
	public void closeDockerContainer(String containerId) {
		dockerClient.stopContainerCmd(containerId).exec();
		dockerClient.removeContainerCmd(containerId).exec();
	}

	private CreateContainerResponse createContainer(String dockerImageName,
		Map<String, String> hostAndBindDirectory) {
		return dockerClient.createContainerCmd(dockerImageName)
			.withHostConfig(createHostConfig(hostAndBindDirectory))
			.withTty(true)
			.exec();
	}

	private ExecCreateCmdResponse execCreateCmd(String containerId,
		JudgeProblemRequestDto judgeProblemRequestDto,
		Submit submit) {
		return dockerClient.execCreateCmd(containerId)
			.withCmd("sh", "-c", "mkdir " + dockerOutDirectory +
				"&& cp -rT /script ./ && bash run.sh "
				+
				judgeProblemRequestDto.getRoomId() + " " +
				judgeProblemRequestDto.getUserId() + " " +
				judgeProblemRequestDto.getProblemId() + " " +
				submit.getId() + " " +
				secretKey)
			.exec();
	}

	private String createHostScriptPath(ProgrammingLanguage language) {
		return hostScriptPath + language.getName();
	}

	private String createHostTestCasePath(Long problemId) {
		return hostTestcasePath + problemId;
	}

	private String createHostUserCodePath(String uuid,
		ProgrammingLanguage language) {
		return uuid + "/" + ProgrammingLanguageManager.getFileName(language);
	}

	/*
	 * 사용자가 제출한 코드를 파일로 생성
	 * @param code 사용자가 제출한 코드
	 * @param language 사용자가 제출한 코드의 언어
	 */
	private void createUserCodeFile(
		String directoryPath,
		String code) throws IOException {
		BlobId blobId = BlobId.of(bucketName, gcsCodePath + directoryPath);

		// Blob 업로드
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
			.setContentType("text/plain") // 코드의 Content Type 설정
			.build();

		storage.create(blobInfo, code.getBytes(StandardCharsets.UTF_8));
	}

	private HostConfig createHostConfig(
		Map<String, String> hostAndBindDirectory) {
		Bind[] binds = hostAndBindDirectory.entrySet().stream()
			.map(
				entry -> new Bind(entry.getKey(), new Volume(entry.getValue())))
			.toArray(Bind[]::new);

		return new HostConfig().withBinds(new Binds(binds));
	}

	private void validateJudgeProblemRequestDto(
		JudgeProblemRequestDto judgeProblemRequestDto) {
		User user = userService.getUser(judgeProblemRequestDto.getUserId());
		GameSession gameSession = gameService.getGameSession(
			judgeProblemRequestDto.getRoomId());
		Room room = roomService.getRoom(judgeProblemRequestDto.getRoomId());

		// 언어 검증
		if (!gameSession.isMatchUserLanguage(user,
			judgeProblemRequestDto.getProgrammingLanguage())) {
			throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(),
				INVALID_INPUT_VALUE.getStatus());
		}

		// 방이 시작되었는지 검증
		if (!room.isStarted()) {
			throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(),
				INVALID_INPUT_VALUE.getStatus());
		}

		// 사용자가 연결되어 있는지 검증
		if (WebsocketSessionHolder.isNotConnected(user)) {
			throw new Custom4XXException(NOT_CONNECT_USER.getMessage(),
				NOT_CONNECT_USER.getStatus());
		}

		// 사용자가 방에 들어가 있는지 검증
		if (!room.isExistUser(user)) {
			throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(),
				INVALID_INPUT_VALUE.getStatus());
		}

		// 문제가 존재하는지 검증
		if (gameSession.getGameRunningConfig().getProblemInfoList().stream()
			.noneMatch(problemInfo -> problemInfo.getProblem().getId()
				.equals(judgeProblemRequestDto.getProblemId()))) {
			throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(),
				INVALID_INPUT_VALUE.getStatus());
		}
	}
}

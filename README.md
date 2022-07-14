---
__GRADLE PROJECT 구성 방법__
- reusability 우측마우스 클릭
- Configure -> Convert to Gradle Project 클릭
- 다시 reusability 우측마우스 클릭
- Gradle -> Refresh Gradle Project 클릭

	위 내용처럼 하면 프로젝트(reusability-web, reusability-common) 생성됨
---

__LOCAL 개발환경에서 실행 방법__
- reusability-web(backend 역할이라 봄) 프로젝트 WAS 기동
  <br>- 내/외장 톰캣 둘다 지원
  <br>- 8080 포트로 실행 (react 에서 proxy 설정이 8080 포트로 되어 있음)
- reusability-web/reactjs 경로에서 'npm' 명령을 통해 실행
  <br>- Window-PowerShell 에서 해당 경로 이동하여 'npm start' 명령

---

__build 방법__
- Gradle Tasks (안보이면 window -> Show View 에서 검색)
- reusability -> build -> clean & build 실행
- ${workspace}/reusability-web/build/libs/reusability-web-1.0.0-SNAPSHOT.war
  <br>- 해당 war 파일내에 react 포함하여 빌드 되어 있음 (reusability-web/build.gradle 파일 참고)

---
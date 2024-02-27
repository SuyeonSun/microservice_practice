# MSA

- MSA 사용 이유

  ### 모놀리식 아키텍처

    - 장점
        - 필요한 모든 기능을 한번만 호출하기 때문에 복잡한 통신 없이 직접 사용
    - 단점
        - 일부 기능을 수정하거나 업데이트를 하려면 전체 애플리케이션을 재배포해야한다.

  ### MSA

    - 장점
        - 기능 고립성이라는 특징 때문에 일부 서비스가 실패하더라도 전체 시스템에 큰 영향을 미치지 않는다.
    - 단점
        - 서비스 간 통신이 필요하며, 서로 간 연결 구축 및 관리의 복잡성이 증가한다.
- 참고 강의

  [https://www.youtube.com/watch?v=mPPhcU7oWDU&t=4856s](https://www.youtube.com/watch?v=mPPhcU7oWDU&t=4856s)


### 프로젝트 설계

- 모듈형으로 프로젝트를 설계한다.
- 서버 별로 포트를 application.yml에 지정한다.

![Untitled](../9653c21e-64f4-4add-8147-f3aee003fbdf_Export-42fbb174-464a-4db4-af0e-e13346273992/MSA%20717eff4765464bf7bb16c6a0664c3dc4/Untitled.png)

### 서버 간 통신

- Spring Boot는 Web Client를 사용하기를 권장한다. Web Client는 동기, 비동기, 스트리밍 서비스를 지원하기 때문이다.
- 마이크로 서비스간의 통신을 비동기 식으로 처리가 가능해 짐에 따라서 마이크로 서비스 간의 통신은 비동기 방식으로 구현하는 추세이다.
- 서버 간 통신에는 동기, 비동기 통신 방식이 존재한다.
    - 동기

      ![Untitled](../9653c21e-64f4-4add-8147-f3aee003fbdf_Export-42fbb174-464a-4db4-af0e-e13346273992/MSA%20717eff4765464bf7bb16c6a0664c3dc4/Untitled%201.png)

        - 개념
            - 동기식 Request and Response 방식의 Antipattern은 위의 그림처럼 서비스 간 호출을 연속으로 가져 가는 방식이다.
        - 단점
            - 마이크로 서비스 간 HTTP 종속성이 있으면 마이크로 서비스가 자체적이지 않게 되며, 해당 체인의 서비스 중 하나가 제대로 수행되지 않는 즉시 성능에 영향을 받는다.
            - 쿼리 요청과 같은 마이크로 서비스 간 동기 종속성을 추가 하면 할수록 전체 응답 시간은 악화된다.

          → 그래서 이러한 장애의 파급효과 및 의존관계를 낮추기 위한 다른 통신 방법이 필요하다.

    - 비동기

      ![Untitled](../9653c21e-64f4-4add-8147-f3aee003fbdf_Export-42fbb174-464a-4db4-af0e-e13346273992/MSA%20717eff4765464bf7bb16c6a0664c3dc4/Untitled%202.png)

        - 개념
            - 메시지 기반의 비 동기식(Asynchronous) 호출
            - 동기식 호출처럼 응답을 기다리지 않는다. 메시지를 보낸 다음에 응답을 기다리지 않고 자신의 일을 처리한다. 물론 보낸 결과가 어떻게 되었는지 응답을 받지 않으니 동기식처럼 완결성을 보장할 수는 없다.
            - 따라서 이를 보장하기 위한 메커니즘이 필요한데 보통 아파치 카프카(Apache Kafka), 래빗엠큐(RabbitMQ), 액티브엠큐(ActiveMQ) 같은 메시지 브로커(Message broker) 사용한다.
        - 장점
            - 소비자가 실패하더라도 발신자는 여전히 메시지를 보낼 수 있다.
        - 단점
            - 메세지 큐에 메시지가 쌓일수록 앞선 메시지를 처리하느라 늦게 들어온 메시지는 엔드투엔드 대기 시간이 그만큼 길어질 수 있다.


### API Gateway

- 개념
    - API Gateway는 API 서버 앞단에서 모든 API 서버들의 엔드포인트를 단일화하여 묶어주고 인증과 인가 기능에서 부터 메세지에 따라 여러 서버로 라우팅하는 고급 기능까지 많은 기능을 담당하는 또 하나의 서버이다.
    - API Gateway는 마치 프록시 서버처럼 API 앞에서 모든 API에 대한 엔드포인트를 통합하는 등의 기능을 제공하는 미들웨어다.
    - API에 대한 인증이나, 로깅과 같은 공통 기능에 대해서, API Gateway로 공통 기능을 처리하게 되면, API 자체는 비지니스 로직에만 집중을 하여 개발에 있어서의 중복 등을 방지 할 수 있다.

### Spring Cloud Netflix Eureka

- 마이크로 서비스의 정보를 Registry에 등록할 수 있도록 하고 동적인 탐색과 로드 밸런싱을 제공한다.
- Eureka에서의 Discovery란?
    - 각 서비스 위치가 등록된 서버에서 특정 작업을 위한 서버의 위치를 파악하는 작업
    - 각 서비스 인스턴스들이 동적으로 확장, 축소 되어도 인스턴스 상태를 하나의 서비스로 관리할 수 있는 서비스
    - MSA에선 Service의 IP와 Port가 일정하지 않고 지속적을 변화한다. 때문에, Client에 Service의 정보를 수동으로 입력하는 것은 한계가 분명하다. 즉, Service Discovery는 MSA의 상황에 적합하다.
- Eureka에서의 Registry란?
    - 각 서비스가 자신의 위치(IP) 정보를 특정 서버에 등록하는 작업
- [http://localhost:8761/](http://localhost:8761/) 에서 확인 가능

### **각 애플리케이션에 랜덤 포트를 부여하는 이유**

랜덤포트의 사용 여부는 마이크로서비스가 독립적으로 사용되는지 LB(로드밸랜서)로 구성되어 사용되는지, 사용시 문제가 되었을 때 복귀해야 하는 구조는 어떻게 되어 있는지 등에 따라 결정하실 수 있습니다. 강의에서 설명 드렸던 랜덤포트(PORT: 0 설저)에 대한 설정은 LB사용을 예시로 보여드리기 위함이었고, 고정으로 설정한 상탱에서 진행하셔도 문제는 없습니다. 다만, Service Discovery에서 등록된 서비스들이 포트 간의 충돌이 발생할 경우 해당 서비스를 우회할 수 있는 포트를 지정하여 새롭게 실행하지 않고, 충돌이 발생한 서비스는 중지 시켜 버리게 됩니다. 포트를 잘 구분하여 서비스들을 배포한다고 했을때는 문제가 없을 수도 있습니다.

그런데, 서비스에서 LB를 사용하도록 되어 있다면, LB에 연결되는 다수의 동일 서비스들은 같은 형태(코드)로 작성되어진 애플리케이션인데, 매번 실행할 때마다 포트를 변경하는 작업을 하기에는 번거로울 수 있습니다. 이때 랜덤포트로 실행하며 임의의 사용가능한 포트가 할당 되고, Service Discovery에 등록될 때는 서비스명으로 사용되어지기 때문에, 여러 개의 서비스가 연결되어 있어도 하나의 서비스명이 요청을 받아 해당 서비스들에 요청처리를 배분해 줄 수 있게 됩니다.

실제로 MSA 형태로 구성된 애플리케이션들은 컨테이너 가상화 형태로 운영되는 경우가 대부분인데, Docker나 Kubernetes에서 컨테이너를 기동할 때는 외부와 연결되기 위한 포트를 랜덤포트로 설정(범위는 지정할 수 있습니다)되어 사용됩니다. 컨테이너 자체는 독립적인 IP를 가지고 있는 VM이라고 보시면 되고, 이 안에서 사용되는 포트는 어떠한 포트로 지정되어 있더라도 상관없이, 컨테이너 외부하고의 연결만 잘 되어 서비스 되는 구조라고 이해하시면 좋을 것 같습니다.

### Flow

- gateway ← eureka server  ← product server, order server
- 주기적으로 (기본값이 30초) eureka -> apigateway로 등록/해제 된 서비스들의 정보를 전달해 주면, apigateway-service가 해당 정보를 기억하고 있다가, 자신이 사용해야 하는 시점에 해당 서비스로 요청을 전달하게 된다.

### Spring Cloud Gateway

- trouble shooting
    - spring-cloud-starter-gateway-mvc와 spring-cloud-starter-gateway는 다르다.

        ```java
        <dependency>
        	<groupId>org.springframework.cloud</groupId>
          <!--<artifactId>spring-cloud-starter-gateway-mvc</artifactId>-->
          <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        ```


### keyClock

- 개념
    - Keycloak은 ID 및 액세스 관리 솔루션을 제공하는 오픈소스
    - 인증(Authentication)과 인가(Authorization)을 쉽게 해주고 SSO(Single-Sign-On)을 가능하게 해주는 오픈소스
- SSO (Single-Sign-On)
    - 한번의 로그인을 통해 그와 연결된 여러가지 다른 사이트들을 자동으로 접속하여 이용할 수 있도록 하는 방법
    - 예를 들어 하나의 회사 내부에서 다양한 시스템을 운영하고 있는 경우, 시스템 각각에 대해 사원 정보가 중복으로 존재할 필요가 없기에 SSO 인증 방식으로 사용하도록 하는 것이다.
- local run

    ```java
    // run
    docker run -p 8181:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin [quay.io/keycloak/keycloak:23.0.7](http://quay.io/keycloak/keycloak:23.0.7) start-dev
    
    // 접속
    [http://localhost:8181/](http://localhost:8181/)
    
    // sign in
    Username: admin, Password: admin
    
    // create realm
    spring-boot-microservices-realm
    
    // create client
    Client ID: spring-cloud-client
    Client authentication -> on
    Authentication flow: Standard flow -> off
                       : Direct access grants -> off
                       : Service accounts roles -> on
    ```
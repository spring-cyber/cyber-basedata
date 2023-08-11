FROM 113.108.106.175:444/cmp/java:11

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/cyber-basedata-0.0.1.jar /home
WORKDIR /home
ENTRYPOINT ["java", "-jar", "cyber-basedata-0.0.1.jar"]

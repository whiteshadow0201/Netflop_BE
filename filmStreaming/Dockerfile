# Sử dụng image JDK 21 từ Eclipse Temurin làm base image
FROM eclipse-temurin:21 AS build

# Cài đặt ffmpeg và các dependencies cần thiết
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean
# Tạo thư mục /opt/app để chứa ứng dụng
RUN mkdir /opt/app

# Copy Maven Wrapper vào thư mục làm việc
COPY mvnw /opt/app/
COPY .mvn /opt/app/.mvn/

# Thay đổi quyền truy cập của file Maven Wrapper để cho phép thực thi
RUN chmod +x /opt/app/mvnw

# Copy file pom.xml và các mã nguồn vào thư mục làm việc
COPY src /opt/app/src
COPY pom.xml /opt/app/

# Di chuyển đến thư mục làm việc
WORKDIR /opt/app

# Chạy với quyền root
USER root

# Chạy Maven Wrapper để build project
RUN ./mvnw --batch-mode  clean package

# Sử dụng image JDK 21 với JRE để tạo image runtime
FROM eclipse-temurin:21-jre

# Tạo thư mục /app để chứa file jar của ứng dụng
RUN mkdir /app

# Copy file jar đã build từ lớp build vào lớp runtime
COPY --from=build /opt/app/target/japp.jar /app/japp.jar

# Định nghĩa lệnh CMD để chạy ứng dụng khi container được khởi chạy
CMD ["java", "-jar", "/app/japp.jar"]

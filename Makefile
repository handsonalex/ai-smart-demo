.PHONY: build test clean run pmd

# 编译（跳过测试）
build:
	mvn clean install -DskipTests

# 运行测试
test:
	mvn test

# PMD 检查
pmd:
	mvn pmd:check

# 清理
clean:
	mvn clean

# 启动应用
run:
	mvn -pl smart-starter spring-boot:run

# 完整构建（编译 + 测试 + PMD）
all: build pmd test

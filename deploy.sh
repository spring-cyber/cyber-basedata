#!/bin/sh

# 使用说明，用来提示输入参数
usage() {
	echo "Usage: sh 执行脚本.sh [port|start|stop|rm]"
	exit 1
}

# 开启所需端口
port(){
	firewall-cmd --add-port=8083/tcp --permanent
	service firewalld restart
}

# 启动程序模块（必须）
start(){
	docker-compose up -d cyber-basedata
}

# 关闭所有环境/模块
stop(){
	docker-compose stop cyber-basedata
}

# 删除所有环境/模块
rm(){
	docker-compose rm cyber-basedata
}

# 根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
"port")
	port
;;
"start")
	start
;;
"stop")
	stop
;;
"rm")
	rm
;;
*)
	usage
;;
esac

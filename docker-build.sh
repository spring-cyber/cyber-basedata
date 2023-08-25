#!/bin/sh

if [ "$1" = "false" ]; then
    exit
fi

set -e

handle_error() {
  echo "发生错误，即将退出！"
  exit 1
}

trap handle_error ERR

v=''

while [[ ! $v ]]
do
  read -p "请输入需要构建的镜像版本号例(0.0.1): " v
done

echo $2:$v

docker build -t $2:$v .
docker push $2:$v

echo Packaged successfully
echo Packaged path: $2
echo Packaged version: $v
echo Packing completion time: $(date +"%Y年%m月%d日 %H时%M分%S秒")

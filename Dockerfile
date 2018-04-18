FROM gradle:jdk8

# ENV http_proxy http://proxy.iwilab.com:8088
# ENV https_proxy http://proxy.iwilab.com:8088

ADD . /app
WORKDIR /app
CMD ["gradle", "runApp"]

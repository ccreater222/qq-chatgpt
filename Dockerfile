FROM 11.0.16-jdk
COPY . /srv/qq-chatgpt
WORKDIR /srv/qq-chatgpt
CMD ["tail", "-f", "/etc/passwd"]
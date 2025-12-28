FROM ubuntu:latest
LABEL authors="ruslanagaev"

ENTRYPOINT ["top", "-b"]
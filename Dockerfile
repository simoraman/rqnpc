FROM clojure
COPY . /usr/src/rqnpc
WORKDIR /usr/src/rqnpc
RUN lein uberjar
CMD java -cp target/rqnpc.jar clojure.main -m rqnpc.server
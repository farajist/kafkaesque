![](https://github.com/rcardin/kafkaesque/workflows/Kafkaesque/badge.svg)

# Kafkaesque
_Kafkaesque_ is a test library whose aim is to make the experience in testing Kafka application less 
painful. By now, the project is in its early stage, defining the API that we will implement in the near 
future.

Every help will be very useful :)

The library allows to test the following use cases:

## Use Case 1: The Application Produces Some Messages on a Topic
```
kafkaesque
  .usingBroker(embeddedBroker)
  .consume() // <-- create the builder KafkaesqueConsumerBuilder
  .fromTopic("topic-name")
  .withDeserializers(keyDeserializer, valueDeserializer)
  .waitingAtMost(10, SECONDS)
  .expecting() // <-- build method that effectively consumes new KafkaesqueConsumer().poll()
  .havingRecordsSize(3) // <-- from here we use a ConsumedResult
  .havingHeaders(headers -> {
    // Assertions on headers
  })
  .havingKeys(keys -> {
    // Assertions on keys
  })
  .havingPayloads(payloads -> {
    // Asserions on payloads
  });
```

## Use Case 2: The Application Consumes Some Messages from a Topic

```
kafkaesque
  .usingBroker(embeddedBroker)
  .produce() // <-- create the builder KafkaesqueProducerBuilder
  .toTopic("topic-name")
  .withDeserializers(keyDeserializer, valueDeserializer)
  .messages( /* Some list of messages */)
  .waitingAtMostForEachAck(100, MILLISECONDS) // Waiting time for each ack from the broker
  .waitingForTheConsumerAtMost(10, SECONDS) // Waiting time for the consumer to read one / all the messages
  .expecting() // build method that effectively create a producer
  .assertingAfterEach(message -> { // <- This method produce a message and asserts imme
    // Assertions on the consumer process after the sending of each message
  })
  .assertingAfterAll(messages -> {
    // Assertions on the consumer process after the sending of all the messages
  });
```

More to come!

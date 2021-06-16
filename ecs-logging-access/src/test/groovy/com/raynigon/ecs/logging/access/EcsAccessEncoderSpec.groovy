package com.raynigon.ecs.logging.access

import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.access.spi.ServerAdapter
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.raynigon.ecs.logging.access.context.IAccessLogContext
import com.raynigon.ecs.logging.access.event.EcsAccessEvent
import com.raynigon.ecs.logging.access.event.EcsAccessLogEvent
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.raynigon.ecs.logging.LoggingConstants.SERVICE_NAME_PROPERTY

class EcsAccessEncoderSpec extends Specification {


    private EcsAccessEncoder encoder = new EcsAccessEncoder()

    def "encoding with minimal event"() {
        given:
        HttpServletRequest request = Mock()
        HttpServletResponse response = Mock()
        ServerAdapter adapter = Mock()
        IAccessLogContext context = Mock()
        IAccessEvent event = new EcsAccessEvent(request, response, adapter, context)

        and:
        adapter.buildResponseHeaderMap() >> Map.of()
        context.getProperty(SERVICE_NAME_PROPERTY) >> "access-test-app"

        when:
        def binaryResult = encoder.encode(event)
        def result = (new ObjectMapper()).readValue(binaryResult, Map.class)

        then:
        result.containsKey("ecs.version")
        result.containsKey("event.dataset")
        result.containsKey("@timestamp")
        result.containsKey("service.name")

        and:
        result["ecs.version"] == "1.7"
        result["event.dataset"] == "access"
        result["service.name"] == "access-test-app"
    }
}
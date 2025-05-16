//package com.sage.sage.microservices.config
//
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito.*
//import org.springframework.http.HttpMethod
//import org.springframework.mock.http.server.reactive.MockServerHttpRequest
//import org.springframework.mock.web.server.MockServerWebExchange
//import org.springframework.web.server.WebFilterChain
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import java.net.URI
//
//class ResponseHeaderWebFilterTest {
//
//    @Test
//    fun `filter should add Content-Type header`() {
//        // Given
//        val requestBuilder = MockServerHttpRequest.method(HttpMethod.GET, URI("hjudfghs"))
//        val filter = ResponseHeaderWebFilter()
//        val exchange = MockServerWebExchange.builder(requestBuilder).build()
//        val chain = mock(WebFilterChain::class.java)
//        `when`(chain.filter(exchange)).thenReturn(Mono.empty())
//
//        // When
//        val result = filter.filter(exchange, chain)
//
//        // Then
//        StepVerifier.create(result)
//            .verifyComplete()
//
//        verify(chain, times(1)).filter(exchange)
//    }
//}
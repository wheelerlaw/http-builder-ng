/*
 * Copyright (C) 2017 David Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovyx.net.http.tk

import com.stehno.ersatz.Decoders
import com.stehno.ersatz.Encoders
import com.stehno.ersatz.ErsatzServer
import org.jsoup.Jsoup

import static com.stehno.ersatz.ContentType.*

/**
 * Base test kit for testing HTTP method handling by different client implementations.
 */
abstract class HttpMethodTestKit extends TestKit {

    protected static final String OK_TEXT = 'ok-text'
    protected static final String OK_JSON = '{"value":"ok-json"}'
    protected static final String OK_XML = '<?xml version="1.0"?><message value="ok-xml"/>'
    protected static final String OK_HTML = '<html><body>ok-html</body>'
    protected static final String OK_CSV = 'alpha,bravo,charlie\none,two,three'
    protected static final Object REQUEST_BODY = [alpha: "bravo", charlie: 42]
    protected static final String REQUEST_BODY_JSON = '{"alpha":"bravo","charlie":42}'
    protected static final OK_XML_DOC = new XmlSlurper().parseText(OK_XML)
    protected static final OK_CSV_DOC = [['alpha', 'bravo', 'charlie'], ['one', 'two', 'three']]
    protected static final OK_HTML_DOC = Jsoup.parse(OK_HTML)

    // FIXME: still need a common testing area for HttpBuilder configuration methods
    // FIXME: test configuration overrided (for clients)

    protected final ErsatzServer ersatzServer = new ErsatzServer({
        enableAutoStart()
        enableHttps()

        encoder TEXT_PLAIN, String, Encoders.text
        encoder TEXT_HTML, String, Encoders.text
        encoder 'text/csv', String, Encoders.text
        encoder TEXT_JSON, String, Encoders.json
        encoder APPLICATION_XML, String, Encoders.text

        decoder TEXT_JSON, Decoders.utf8String
        decoder APPLICATION_JSON, Decoders.utf8String
        decoder APPLICATION_XML, Decoders.utf8String
        decoder TEXT_HTML, Decoders.utf8String
        decoder 'text/csv', Decoders.utf8String
        decoder TEXT_PLAIN, Decoders.utf8String
        decoder APPLICATION_URLENCODED, Decoders.urlEncoded
    })

    // TODO: will autocleanup work in this case?
    def cleanup() {
        ersatzServer.stop()
    }

    // FIXME: get rid of these!

    static String htmlContent(String text = 'Nothing special') {
        "<html><body><!-- a bunch of really interesting content that you would be sorry to miss -->$text</body></html>" as String
    }

    static String xmlContent(String text = 'Nothing special') {
        "<?xml version=\"1.0\"?><root><child><elt name='foo' /><text>$text</text></child></root>" as String
    }

    static String jsonContent(String text = 'Nothing special') {
        """
            {
                "items":[
                    {
                        "name":"alpha",
                        "score":123,
                        "text": "${text}"
                    }
                ]
            }
        """.stripIndent()
    }

    protected static String findExceptionMessage(Exception ex) {
        ex.cause ? findExceptionMessage(ex.cause) : ex.message
    }
}

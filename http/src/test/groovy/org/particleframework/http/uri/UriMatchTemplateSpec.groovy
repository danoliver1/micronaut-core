/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.http.uri

import org.particleframework.http.uri.UriMatchInfo
import org.particleframework.http.uri.UriMatchTemplate
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class UriMatchTemplateSpec extends Specification {

    @Unroll
    void "test compareTo for #left and #right"() {
        given:
        UriMatchTemplate leftTemplate = new UriMatchTemplate(left)
        UriMatchTemplate rightTemplate = new UriMatchTemplate(right)

        expect:
        leftTemplate.compareTo(rightTemplate) == result

        where:
        left           | right          | result
        "/book"        | "/{name}"      | 1
        "/"            | "/"            | 0
        "/"            | "/book"        | -1
        "/book/foo"    | "/book"        | 1
        "/book/{name}" | "/book"        | 1
        "/book/{name}" | "/book/{test}" | 0
        "/book/{name}" | "/book/test"   | -1

    }

    @Unroll
    void "Test URI template #template matches #uri when nested with #nested"() {
        given:
        UriMatchTemplate matchTemplate = new UriMatchTemplate(template)
        Optional<UriMatchInfo> info = matchTemplate.nest(nested).match(uri)

        expect:
        info.isPresent() == matches
        info.orElse(null)?.variables == variables


        where:
        template      | uri                  | nested                | matches | variables
        "/"           | "/authors/2"         | '/authors{/authorId}' | true    | [authorId: '2']
        "/books{/id}" | "/books/1/authors/2" | '/authors{/authorId}' | true    | [id: '1', authorId: '2']
        ""            | "/authors/2"         | '/authors{/authorId}' | true    | [authorId: '2']

    }

    @Unroll
    void "Test URI template #template matches #uri"() {
        given:
        UriMatchTemplate matchTemplate = new UriMatchTemplate(template)
        Optional<UriMatchInfo> info = matchTemplate.match(uri)

        expect:
        info.isPresent() == matches
        info.orElse(null)?.variables == variables


        where:
        template                         | uri                   | matches | variables
        ""                               | ""                    | true    | [:]
        "/"                              | "/"                   | true    | [:]
        "/"                              | ""                    | true    | [:]
        "/books{/id}/authors{/authorId}" | "/books/1/authors/2"  | true    | [id: '1', authorId: '2']
        "/books{/path:.*}{.ext:xml}"     | '/books/foo/bar.xml'  | true    | [path: '/foo/bar', ext: 'xml']
        "/books{/path:.*}{.ext:xml}"     | '/books/foo/bar.json' | false   | null
        "/books/{id:\\d+}"               | '/books/test'         | false   | null
        "/books/{id:\\d+}"               | '/books/101'          | true    | [id: '101']
        "/books/{id:\\d+}"               | '/books'              | false   | null
        "/books{/path:.*}"               | '/books'              | true    | [path: '']
        "/books{/path:.*}"               | '/books/foo/bar'      | true    | [path: '/foo/bar']
        "/books{/path:.*}{.ext}"         | '/books/foo/bar.xml'  | true    | [path: '/foo/bar', ext: 'xml']
        "/books{/path:.*?}{.ext:?}"      | '/books/foo/bar.xml'  | true    | [path: '/foo/bar', ext: 'xml']
        "/books{/path:.*}{.ext:?}"       | '/books/foo/bar'      | true    | [path: '/foo/bar', ext: null]
        "/books/{id}"                    | '/books'              | false   | null
        "/books/{id}"                    | '/books/1'            | true    | [id: '1']
        "/books/{id}"                    | '/books/test'         | true    | [id: 'test']
        "/books/{id:2}"                  | '/books/1'            | true    | [id: '1']
        "/books/{id:2}"                  | '/books/100'          | false   | null
        "/books{/id:?}"                  | '/books'              | true    | [id: null]
        "/books{/id}{.ext}"              | '/books/1.xml'        | true    | [id: '1', ext: 'xml']
        "/books{/id}{.ext}"              | '/books/1'            | false   | null
        "/books{/id}{.ext:?}"            | '/books/1'            | true    | [id: '1', ext: null]
        "/books{/id}{.ext:?}"            | '/books/1.xml'        | true    | [id: '1', ext: 'xml']
        "/books{/id:?}{.ext:?}"          | '/books'              | true    | [id: null, ext: null]
        "/books{/action}{/id:?}"         | '/books/show'         | true    | [id: null, action: 'show']
        "/books{/action}{/id:?}"         | '/books/show/1'       | true    | [id: '1', action: 'show']
        "/books{/action}{/id:2}"         | '/books/show/1'       | true    | [id: '1', action: 'show']
        "/books{/action}{/id:2}"         | '/books/show/100'     | false   | null
        "/book{/id}"                     | '/book/1'             | true    | [id: '1']
        "/book{/id}"                     | '/book'               | false   | null
        "/book{/id}"                     | '/book'               | false   | null
        "/book{/action}{/id}"            | '/book/show/1'        | true    | [action: 'show', id: '1']
        "/book{/action}{/id}"            | '/book/1'             | false   | null
        "/book/show{/id}"                | '/book/show/1'        | true    | [id: '1']
        "/book/show{/id}"                | '/book/1'             | false   | null
        "/books{?max,offset}"            | "/books"              | true    | [:]
        "/books{#hashtag}"               | "/books"              | true    | [:]
    }
}

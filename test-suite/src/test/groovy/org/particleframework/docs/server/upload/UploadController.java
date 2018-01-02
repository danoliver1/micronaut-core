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
package org.particleframework.docs.server.upload;

// tag::imports[]
import io.reactivex.Single;
import org.particleframework.http.*;
import org.particleframework.http.annotation.Controller;
import org.particleframework.http.multipart.FileUpload;
import org.particleframework.web.router.annotation.Post;
import org.reactivestreams.Publisher;

import java.util.Optional;
// end::imports[]

/**
 * @author Graeme Rocher
 * @since 1.0
 */
// tag::class[]
@Controller
public class UploadController {
// end::class[]

    // tag::upload[]
    @Post(value = "/", consumes = MediaType.MULTIPART_FORM_DATA) // <1>
    public Single<HttpResponse<String>> upload(FileUpload file, Optional<String> anotherAttribute) { // <2>
        Publisher<Boolean> uploadPublisher = file.transferTo(file.getFilename()); // <3>
        return Single.fromPublisher(uploadPublisher)  // <4>
                    .map(success -> {
                        if (success) {
                            return HttpResponse.ok("Uploaded");
                        } else {
                            return HttpResponse.<String>status(HttpStatus.CONFLICT)
                                               .body("Upload Failed");
                        }
                    });
    }
    // end::upload[]

}

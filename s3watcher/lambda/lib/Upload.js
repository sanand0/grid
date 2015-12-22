const request = require('request');
const Rx = require('rx');
const Logger       = require('./Logger');


module.exports = {
    buildUpload: function(config, s3Event) {
         const headers = {
            'X-Gu-Media-Key': config.apiKey
        };

         const buildUploadedBy = function(path){
             if (path.length > 1) {
                 return path[0];
             } else {
                 throw new Error("File uploaded to root folder.");
             }
         };

         const uploadedBy = buildUploadedBy(s3Event.path);

         return {
             key: config.apiKey,
             url: config.baseUrl,
             path: "/imports",
             size: s3Event.size,
             headers: headers,
             params: {
                 filename: s3Event.filename,
                 uploadedBy: uploadedBy,
                 stage: config.stage
             }
         };
    },
    postUri: function(upload, imageUri) {
        function uploadResult(response) {
            return {
                statusCode: response.statusCode,
                succeeded: response.statusCode == 202,
                uploadedBy: upload.params.uploadedBy,
                stage: upload.params.stage
            };
        }

        // FIXME: refactor so as to avoid having to mutate params like this
        upload.params.uri = imageUri;

        const url = upload.url + upload.path;
        const options = {
            url: url,
            headers: upload.headers,
            qs: upload.params
        };

        const uploadRequest = request.post(options);

        Logger.log(upload.params.stage, 'POST', options);

        return Rx.Observable.create(function(observer){
            uploadRequest.on("response", function(response){
                Logger.log(upload.params.stage, 'GOT RESPONSE', response);
                observer.onNext(uploadResult(response));
                observer.onCompleted();
            });
            uploadRequest.on("error", function(error) {
                Logger.log(upload.params.stage, 'GOT ERROR', error);
            });
            uploadRequest.on("error", observer.onError.bind(observer));
            Logger.log(upload.params.stage, 'LISTENERS SET', {});
        });
    }
};

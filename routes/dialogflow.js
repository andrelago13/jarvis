var express = require('express');
var router = express.Router();
var dialogflow = require('./../util/dialogflow');

router.get('/', function (req, res, next) {
    if (req.body.result === undefined || req.body.result.source !== dialogflow.agent) {
        // Unauthorized request - only DialogFlow agent is supported
        if(req.body.result !== undefined) {
            console.log("Unauthorized source: " + req.body.result.source)
        }
        
        res.status(401);
        res.setHeader('Content-Type', 'application/json');
        res.send(JSON.stringify({
            "speech": "Sorry, this query provider is not yet supported.",
            "displayText": "Sorry, this query provider is not yet supported."
        }));
        return;
    }

    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify({
        "speech": "Sorry, I am currently unable to answer this query.",
        "displayText": "Sorry, I am currently unable to answer this query."
    }));
});
/*
{
    id: '1503c4ba-948c-4a40-9cfc-cf224a37834b',
    timestamp: '2018-01-08T17:23:10.919Z',
    lang: 'en',
    result: {
        source: 'agent',
        resolvedQuery: 'turn on the lights',
        speech: '',
        action: '',
        actionIncomplete: false,
        parameters: {
            actuator: 'light switch'
        },
        contexts: [],
        metadata: {
            intentId: 'e7e07192-7e72-48d5-b702-aea5f8a79f4b',
            webhookUsed: 'true',
            webhookForSlotFillingUsed: 'true',
            intentName: 'Turn the thing on'
        },
        fulfillment: {
            speech: 'Done',
            messages: [Array]
        },
        score: 1
    },
    status: {
        code: 200,
        errorType: 'success',
        webhookTimedOut: false
    },
    sessionId: '730cbf64-7ff0-4b3d-8140-fa24b98193c8'
}
*/

module.exports = router;
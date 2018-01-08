var express = require('express');
var router = express.Router();

router.post('/', function(req, res, next) {
    console.log(req);
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify({ 
        "speech": "Barack Hussein Obama II was the 44th and current President of the United States.",
        "displayText": "Barack Hussein Obama II was the 44th and current President of the United States, and the first African American to hold the office. Born in Honolulu, Hawaii, Obama is a graduate of Columbia University   and Harvard Law School, where ",
        "source": "DuckDuckGo"
    }));
});

module.exports = router;

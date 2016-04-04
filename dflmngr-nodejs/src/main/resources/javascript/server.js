var express = require('express');
var fs = require('fs');
var request = require('request');
var cheerio = require('cheerio');
var morgan = require('morgan');
var fsr = require('file-stream-rotator');
var app = express();

var logDirectory = __dirname + '/log'
fs.existsSync(logDirectory) || fs.mkdirSync(logDirectory)

var accessLogStream = fsr.getStream({
  date_format: 'YYYYMMDD',
  filename: logDirectory + '/access-%DATE%.log',
  frequency: 'daily',
  verbose: false
})

app.use(morgan('combined', {stream: accessLogStream}))

app.get('/scrape', function(req, res){

url = req.query.url;
team = req.query.team;

request(url, function(error, response, html){
    if(!error){
        var $ = cheerio.load(html);
	
	var jsonData = [];

	if(team=="home") {
		$('#homeTeam-advanced').filter(function(){
			$(this).children('tbody').first().children('tr').each(function(i, elem) {
				var jsonRec = {name : "", jumper : "", kicks : "", handballs : "", disposals : "", marks : "", hitouts : "", freesFor : "", freesAgainst : "", tackles : "", goals : "", behinds : ""}
				$(this).children().each(function(j, elem) {
					var data;
							
					if(j==0) {
						data = $(this).children('.full-name').first().text().trim().replace(/(\n|\r|\t)+$/, '');
					} else {
						data = $(this).text().trim().replace(/(\n|\r|\t)+$/, '');
					}
					
					switch(j) {
						case 0 : jsonRec.name = data; break;
						case 1 : jsonRec.jumper = data; break;
						case 2 : jsonRec.kicks = data; break;
						case 3 : jsonRec.handballs = data; break;
						case 4 : jsonRec.disposals = data; break;
						case 9 : jsonRec.marks = data; break;
						case 12 : jsonRec.hitouts = data; break;
						case 17 : jsonRec.freesFor = data; break;
						case 18 : jsonRec.freesAgainst = data; break;
						case 19 : jsonRec.tackles = data; break;
						case 23 : jsonRec.goals = data; break;
						case 24 : jsonRec.behinds = data; break;
					}
				});
				jsonData[i] = jsonRec;
			});
		})
	} else {
				$('#awayTeam-advanced').filter(function(){
			$(this).children('tbody').first().children('tr').each(function(i, elem) {
				var jsonRec = {name : "", jumper : "", kicks : "", handballs : "", disposals : "", marks : "", hitouts : "", freesFor : "", freesAgainst : "", tackles : "", goals : "", behinds : ""}
				$(this).children().each(function(j, elem) {
					var data;
							
					if(j==0) {
						data = $(this).children('.full-name').first().text().trim().replace(/(\n|\r|\t)+$/, '');
					} else {
						data = $(this).text().trim().replace(/(\n|\r|\t)+$/, '');
					}
					
					switch(j) {
						case 0 : jsonRec.name = data; break;
						case 1 : jsonRec.jumper = data; break;
						case 2 : jsonRec.kicks = data; break;
						case 3 : jsonRec.handballs = data; break;
						case 4 : jsonRec.disposals = data; break;
						case 9 : jsonRec.marks = data; break;
						case 12 : jsonRec.hitouts = data; break;
						case 17 : jsonRec.freesFor = data; break;
						case 18 : jsonRec.freesAgainst = data; break;
						case 19 : jsonRec.tackles = data; break;
						case 23 : jsonRec.goals = data; break;
						case 24 : jsonRec.behinds = data; break;
					}
				});
				jsonData[i] = jsonRec;
			});
		})
	}
}

res.setHeader('Content-Type', 'application/json');
res.send(JSON.stringify(jsonData, null, 4));

    }) ;
})

app.listen('8081')
console.log('Magic happens on port 8081');
exports = module.exports = app;
const Nightmare = require('nightmare');
const nightmare = Nightmare({show: true});


nightmare
    .goto('https://www.linode.com/docs')
    .insert('#gsc-i-id1', 'ubuntu')
    .click('input.gsc-search-button-v2')
    .wait('#search-results')
    .evaluate(function() {
            let searchResults = [];

            const results =  document.querySelectorAll('h6.library-search-result-title a');
            results.forEach(function(result) {
                    let row = {
                                    'title':result.innerText,
                                    'url':result.href
                              }
                    searchResults.push(row);
            });
            return searchResults;
    })
    .end()
    .then(function(result) {
            result.forEach(function(r) {
                    console.log('Title: ' + r.title);
                    console.log('URL: ' + r.url);
            })
    })
    .catch(function(e)  {
            console.log(e);
    });
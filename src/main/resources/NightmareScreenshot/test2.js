var Nightmare = require('nightmare'),
  nightmare = Nightmare({
    show: true
  });

nightmare
  //load a url
  .goto('http://yahoo.com')
  //simulate typing into an element identified by a CSS selector
  //here, Nightmare is typing into the search bar
  .type('input[title="Search"]', 'github nightmare')
  //click an element identified by a CSS selector
  //in this case, click the search button
  .click('#uh-search-button')
  //wait for an element identified by a CSS selector
  //in this case, the body of the results
  .wait('#main')
  //execute javascript on the page
  //here, the function is getting the HREF of the first search result
  .evaluate(function() {
    return document.querySelector('#main .searchCenterMiddle li a')
      .href;
  })
  //end the Nightmare instance along with the Electron instance it wraps
  .end()
  //run the queue of commands specified, followed by logging the HREF
  .then(function(result) {
    console.log(result);
  })
  //catch errors if they happen
  .catch(function(error){
    console.error('an error has occurred: ' + error);
  });
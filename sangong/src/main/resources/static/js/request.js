/*
* @url: url link
* @action: "get", "post"
* @json: {'key1':'value2', 'key2':'value2'}
*/
function doFormRequest(url, action, json)
{
    var form = document.createElement("form");
    form.action = url;
    form.method = action;

    // append input attribute and valus
    for (var key in json)
    {
        if (json.hasOwnProperty(key))
        {
            var val = json[key];
            input = document.createElement("input");
            input.type = "hidden";
            input.name = key;
            input.value = val;

            // append key-value to form
            form.appendChild(input)
        }
    }

    // send post request
    document.body.appendChild(form);
    form.submit();

    // remove form from document
    document.body.removeChild(form);
}

//读取cookies
function getToken(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

    if(arr=document.cookie.match(reg))

        return unescape(arr[2]);
    else
        return null;
}

function getRequest( url, time, callback ){
    var request = new XMLHttpRequest();
    var timeout = false;
    var timer = setTimeout( function(){
        timeout = true;
        request.abort();
    }, time );
    request.open( "GET", url );
    request.onreadystatechange = function(){
        if( request.readyState !== 4 ) return;
        if( timeout ) return;
        clearTimeout( timer );
        if( request.status === 200 ){
            callback( request.responseText );
        }
    }
    request.send( null );
}
function addCard(type, name, address, index){
    if(type == "268"){
        image = "laptop-grey";
    }
    else if(type == "7936"){
        if(name.includes("Goldie")){
            image = "vest";
        }
        else{
            image = "headphones-grey";
        }
    }
    else{
        image = "other-grey";
    }
    var devices = document.getElementById("devices");
    devices.innerHTML += "<div id='" + address + "' class='card' onclick=connect(" + index + ")><div class='vertical-center' style='width:100%;'><div style='float: left'>" + 
        "<img class='image' src='images/" + image + ".png' style='height:5em;'></div>" +
        "<div><p class='deviceName'><b>" + name + "</b></p><p class='address'>" + address + "</p>" +
        "</div></div><div>";
}

function connect(index){
    console.log("HEEEEEEEEEEEEELLLLLLLLLLLPPPPPPPPPPP");
    console.log(index);
    Android.connectDevice(index);
    window.location.href = "data.html";
}
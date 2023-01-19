var activity = "";
var timerStart = 0;
var x = 0;

function load(){
    var deviceConnectedCheck = setInterval(function(){
        var status = Android.checkConnections();
        var deviceStatus = document.getElementById("deviceStatus")
        var bluetoothStatus = document.getElementById("bluetoothStatus");
        if(status[0] == "0"){
            deviceStatus.innerHTML = "Disconnected";
            deviceStatus.classList = "disconnectedStatusText";
        }
        else{
            deviceStatus.innerHTML = "Connected";
            deviceStatus.classList = "connectedStatusText";
        }

        if(status[1] == "0"){
            bluetoothStatus.innerHTML = "Disconnected";
            bluetoothStatus.classList = "disconnectedStatusText";
        }
        else{
            bluetoothStatus.innerHTML = "Connected";
            bluetoothStatus.classList = "connectedStatusText";
        }
        console.log(status);
    }, 5000);
}

function activity_toggle(activity_clicked){
    if(activity == ""){
        activity = activity_clicked;
        document.getElementById(activity).classList = "card active-light";
        document.getElementById(activity + "TimerContainer").style.display = "block";
        document.getElementById(activity + "Image").src = "images/" + activity + "_white.png";
        document.getElementById(activity + "Image").classList.toggle("transition");
        // document.getElementById("state").innerHTML = idToLabel("sleep");
        timer();
        Android.setActivity(activity);
    }
    else {
        prompt(activity_clicked);
        Android.setActivity("unknown");
    }
}

function timer(){
    // Set the date we're counting down to
    timerStart = new Date().getTime();

    // Update the count down every 1 second
    x = setInterval(function() {

    // Get today's date and time
    var now = new Date().getTime();
        
    // Find the distance between now and the count down date
    var distance = now - timerStart;
        
    // Time calculations for days, hours, minutes and seconds
    var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    var seconds = Math.floor((distance % (1000 * 60)) / 1000);
        
    // Output the result in an element with id="demo"
    document.getElementById(activity + "Timer").innerHTML = hours + ":"
    + minutes + ":" + seconds;
    }, 1000);
}

function idToLabel(id){
    console.log(id);
    if(id == "sleep") return "sleeping";
    else if(id == "walking") return "walking";
    else if(id == "sitting") return "sitting";
    else return "Fish";
}

function norms(id){
    
}

tempId = "";
function prompt(id){
    document.getElementById("background").style.display = "block";
    document.getElementById("prompt").style.display = "block";
    document.getElementById("activityPrompt").innerHTML = idToLabel(activity);
    tempId = id;
}

function promptResponse(response){
    console.log(tempId + ", " + activity);
    if(response == "yes"){
        displaySleepNotification();
        document.getElementById(activity).classList = "card";
        document.getElementById(activity+"TimerContainer").style.display = "none";
        document.getElementById(activity+"Image").src = "images/" + activity + ".png";
        document.getElementById(activity+"Image").classList.toggle("transition");
        document.getElementById(activity + "Timer").innerHTML = "";

        if(activity != tempId){
            document.getElementById(tempId).classList = "card active-light";
            document.getElementById(tempId+"TimerContainer").style.display = "block";
            document.getElementById(tempId+"Image").src = "images/" + tempId + "-white.png"
            document.getElementById(tempId+"Image").classList.toggle("transition");
            // document.getElementById("state").innerHTML = idToLabel(tempId);
            timer(tempId);
            activity = tempId;
        }
        else{
            // document.getElementById("state").innerHTML = "Unknown";
            activity = "";
            clearInterval(x);
        }
    }
    document.getElementById("background").style.display = "none";
    document.getElementById("prompt").style.display = "none";

}

function displaySleepNotification(){
    // Animation Text
    text = ""
    timerData = document.getElementById(activity + "Timer").innerHTML.split(":");
    if(timerData[0] != "0"){
        if(parseFloat(timerData[0]) == 1)
            text += timerData[0] + " hour,";
        else
            text += timerData[0] + " hours,";
    }
    if(timerData[1] != "0"){
        if(parseFloat(timerData[0]) == 1)
            text += timerData[1] + " minute, and ";
        else
            text += timerData[1] + " minutes, and ";
    }
    text += timerData[2] + " seconds of " + document.getElementById("activityPrompt").innerHTML + " saved.";
    document.getElementById("notifText").innerHTML = text;

    // Animation
    animateNotification();
}

function animateNotification(){
    var notification = document.getElementById("notification");
    var pos = parseFloat(notification.style.marginTop.slice(0, -2));
    // console.log(pos);
    id = setInterval(frame, 5);
    count = 0;
    newPos = 2.09999999;
    function frame(){
        // console.log("Current Position: " + pos)
        // console.log(pos < 10);
        // console.log(count < 50);
        if(pos < 2){
            pos += 0.05;
            notification.style.marginTop = pos + "em";
        }
        else if(count < 600){
            // console.log(count);
            count++;
        }
        else if(newPos > -10){
            // console.log(newPos);
            newPos -= 0.03;
            notification.style.marginTop = newPos + "em";
        }
        else{
            clearInterval(id);
        }
    }
}

function displayOtherNotification(id){
    document.getElementById("notifText").innerHTML = idToLabel(id) + " recorded.";
    animateNotification();
    Android.saveEvent(idToLabel(id));
}

function back(){
    window.location.href = "connect.html";
    Android.backButton();
}

function review(){
    window.location.href = "review.html";
}

function areYouSure(){
    console.log("Debug: are you sure?");
    document.getElementById("areYouSure").style.display = "inline";
}

function disconnectResponse(res){
    if(res == "yes"){
        back();
    }
    document.getElementById("areYouSure").style.display = "none";
}
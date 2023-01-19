var currentScrollTop = 0;

function scroll(){
    console.log("help");
    var div = document.getElementById("bigCard");
    var initial = (document.documentElement.clientHeight/100) * 25;
    var threshold = (document.documentElement.clientHeight/100) * 10;

    var style = window.getComputedStyle(div);
    console.log(div.scrollTop);
    if (div.scrollTop > 10){
        console.log("hello?");
        div.style.marginTop = threshold;
        // div.scrollTo(0);
    }

    // if (div.scrollTop < initial-threshold) {
    //     console.log(div.scrollTop);
    //     // console.log(document.getElementById("bigCard").currentStyle.marginTop);// -= document.getElementById("bigCard").scrollTop;
    //     var style = window.getComputedStyle(div);
    //     div.style.marginTop = initial - div.scrollTop;
    //     console.log(+style.marginTop.slice(0, -2) - 1);
    //   }
    // if (div.scrollTop > 25 && !up){
    //     div.style.marginTop = (document.documentElement.clientHeight/100) * 10;
    //     up = true;
    // }
    // else if (div.scrollTop <= 25 && up){
    //     div.style.marginTop = (document.documentElement.clientHeight/100) * 25;
    //     up = false;
    // }
}
# project-example

An example project - specifically a data collection app created for Goldilocks. The app uses a webview and html, css, and javascript for the front end and has a webappinterface written in Java to interact with various aspects of the device + all the other bits and pieces such as uploading the data etc. (also written in Java)

# Folders:
**assets** - Assets for the app. Contains all front end html, css and javascript.<br>
**java** - Contains the java code for the application itself.<br>
**res** - Auto-generated misc. files and folders for the application - includes app icons etc.<br>

# Assets
Directory has the html files and folders for everything else, all stylesheets in the css folder, js files in the javascript folder etc.<br> Also has folders for helper libraries bootstrap and chartjs

# Main
Standard java/Android package layout. Folder goldilocksdatacollection has the actual code.<br>
**MainActivity** is the main class for the app, this starts the **Scanner** to connect to nearby devices. Once a device is found and connected to the **Scanner** class creates an instance of the **Bluetooth** class which handles the communication between the phone and device. When the data is received it's then uploaded using the method in the **Uploader** class.<br>
(So **MainActivity** -> **Scanner** -> **Bluetooth** -> **Uploader**)<br>
<br>
The **Mediator** class is used for passing some information between the main activity and the WebAppInterface<br>
**WebAppInterface** is the interface between the webview and the application itself.

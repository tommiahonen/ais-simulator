document.addEventListener("DOMContentLoaded", onStartup);

function onStartup() {
    refreshFilelist()
    onUploadFileChange()
}

function serverFetch(url, elementIdMessage, elementIdClear) {
    fetch(url)
        .then(function (response) {
            return response.text();
        }).then(function (myText) {
            document.getElementById(elementIdMessage).innerHTML = myText
            if (document.getElementById(elementIdClear)) {
                document.getElementById(elementIdClear).innerHTML = '';
            }
            console.log("Servern svarade=" + elementIdMessage)
        }
    )
}

function setNth() {
    let nthValue = document.getElementById("nthfield").value
    serverFetch('/rest/state/setnth/' + nthValue, 'nthMessage', '')
}

function setFilename() {
    let filename = document.getElementById("setfiledropdown").value
    console.log("filename=" + filename)
    serverFetch('/rest/state/setFilename/' + filename, 'filenameMessage', '')
}

function onUploadFileChange() {
    let fileuploadSelectorElement = document.getElementById("fileupload")
    let fileuploadButton = document.getElementById("uploadFileButton")

    if (fileuploadSelectorElement.files[0]) {
        // // A file has been selected: enable upload button.
        fileuploadButton.disabled=false;
    } else {
        // No file has been selected: disable upload button.
        fileuploadButton.disabled=true;
    }
}

function refreshFilelist() {
    fetch("/rest/files/list")
        .then(function (response) {
            return response.json();
        }).then(function (json) {
        updateFilelistDropdownMenu(json)
    })
}

async function uploadFile() {
    let fileupload = document.getElementById("fileupload")
    if (!fileupload.files[0]) {
        // Upload button clicked even though no file was selected: do nothing.
        return
    }

    let formData = new FormData();
    formData.append("newdatafile", fileupload.files[0]);
    fetch('http://localhost:8080/rest/files/upload', {
        method: "POST",
        body: formData
    }).then(function (response) {
        return response.text();
    }).then(function (responseText) {
        document.getElementById("uploadMessage").innerHTML = responseText;
        refreshFilelist()
    })
}

function updateFilelistDropdownMenu(json) {

    let dropdownElement = document.getElementById("setfiledropdown");

    // Delete all existing options from dropdown menu.
    while (dropdownElement.firstChild) {
        dropdownElement.removeChild(dropdownElement.lastChild);
    }

    let newOptionElement = document.createElement("OPTION")
    // Create the first drop-down menu option
    dropdownElement.append(newOptionElement)
    newOptionElement.text = "Please choose a file."
    newOptionElement.disabled = true

    // disable upload file selection if no files have been uploaded yet
    if (json.length==0) {

        document.getElementById("setfiledropdown").disabled=true
        document.getElementById("update").disabled=true
        newOptionElement.text = "Please upload a file first."

    } else {
        document.getElementById("setfiledropdown").disabled=false
        document.getElementById("update").disabled=false
    }



    // Create new option elements and add them to dropdown menu.
    for (let i = 0; i < json.length; i++) {
        console.log("file=" + json[i])
        let newOptionElement = document.createElement("OPTION")
        dropdownElement.append(newOptionElement)
        newOptionElement.value = json[i];
        newOptionElement.text = json[i]
    }

}
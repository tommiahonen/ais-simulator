window.onload = refreshFilelist()

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

function refreshFilelist() {
    fetch("/rest/files/list")
        .then(function (response) {
            return response.json();
        }).then(function (json) {
        updateFilelistDropdownMenu(json)
    })
}

async function uploadFile() {
    console.log("About to upload file...")
    let fileupload = document.getElementById("fileupload")
    let formData = new FormData();
    formData.append("newdatafile", fileupload.files[0]);
    fetch('http://localhost:8080/rest/files/upload', {
        method: "POST",
        body: formData
    }).then(function (response) {
        return response.text();
    }).then(function (responseText) {
        document.getElementById("uploadMessage").innerHTML = responseText;
    })
}

function updateFilelistDropdownMenu(json) {

    let dropdownElement = document.getElementById("setfiledropdown");

    // Delete all existing options from dropdown menu.
    while (dropdownElement.firstChild) {
        dropdownElement.removeChild(dropdownElement.lastChild);
    }

    let newOptionElement = document.createElement("OPTION")
    dropdownElement.append(newOptionElement)
    newOptionElement.text="Please choose a file."
    newOptionElement.disabled=true;

    // Create new option elements and add them to dropdown menu.
    for (let i = 0; i < json.length; i++) {
        console.log("file=" + json[i])
        let newOptionElement = document.createElement("OPTION")
        dropdownElement.append(newOptionElement)
        newOptionElement.value = json[i];
        newOptionElement.text = json[i]

    }

}
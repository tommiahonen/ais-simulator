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
    let filename = document.getElementById("setfilename").value
    console.log("filename=" + filename)
    serverFetch('/rest/state/setFilename/' + filename, 'filenameMessage', '')
}

function refreshFilelist() {
    fetch("/rest/files/list")
        .then(function (response) {
            return response.json();
        }).then(function (json) {

        let filelistHtml = "";
        for (let i = 0; i < json.length; i++) {
            filelistHtml += json[i];
            if (i < json.length - 1) {
                filelistHtml += "<br>";
            }
        }

        document.getElementById("filelist").innerHTML = filelistHtml
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
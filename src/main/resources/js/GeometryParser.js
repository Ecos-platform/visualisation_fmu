
function createPlane(shape, mat) {
    return new THREE.Mesh(new THREE.BoxGeometry(shape.width, shape.height), mat)
}

function createBox(shape, mat) {
    return new THREE.Mesh(new THREE.BoxGeometry(shape.width, shape.height, shape.depth), mat)
}

function createSphere(shape, mat) {
    return new THREE.Mesh(new THREE.SphereGeometry(shape.radius, 32, 32), mat)
}

function createCylinder(shape, mat) {
    return new THREE.Mesh(new THREE.CylinderGeometry(shape.radius, shape.radius, shape.height, 32, 32), mat)
}

function createCapsule(shape, mat) {
    return new Capsule(shape.radius, shape.height, mat, 32, 32)
}

function loadObj(shape, callback) {

    const source = shape.source.replace(/\\/g, "/")
    const objLoader = new THREE.OBJLoader()

    let httpGet = function (url, callback) {
        let xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState === 4) {
                if (xmlHttp.status === 200) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
        xmlHttp.open("GET", url, true); // true for asynchronous
        xmlHttp.send(null);
    }

    let load = function (hasMtl) {
        objLoader.load("/assets?" + source, function (obj) {
            callback(obj)
        })
    }

    let split = source.split("/")
    let mtlUrl = split.pop().replace(".obj", ".mtl")
    let mtlUrlPath = "/assets?" + split.join("/") + "/"
    httpGet(mtlUrlPath + mtlUrl, function (success) {
        if (success) {
            const mtlLoader = new THREE.MTLLoader()
            mtlLoader.crossOrigin = '';
            mtlLoader.setPath(mtlUrlPath)
            mtlLoader.load(mtlUrl, function (materials) {
                materials.preload()
                objLoader.setMaterials(materials)
                load(true)
            })
        } else {
            load(false)
        }
    })
}

function createTrimesh(shape, callback) {
    const ext = shape.source.split(".").pop()
    switch (ext) {
        case "obj":
            loadObj(shape, callback)
            break
        default:
            console.error("Unsupported trimesh extension: " + ext)
            break
    }
}

function createMesh(shape, mat, callback) {
    console.log(shape)
    switch (shape.type) {
        case "plane":
            callback(createPlane(shape, mat))
            break
        case "box":
            callback(createBox(shape, mat))
            break
        case "sphere":
            callback(createSphere(shape, mat))
            break
        case "cylinder":
            callback(createCylinder(shape, mat))
            break
        case "capsule":
            callback(createCapsule(shape, mat))
            break
        case "mesh":
            createTrimesh(shape, callback)
            break
    }
}

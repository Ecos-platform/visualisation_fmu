class VisualizationApp {

    constructor() {

        let that = this
        this.water = undefined
        this.follow = false
        this.objects = {}
        this.trailObjects = []
        this.MAX_LINE_POINTS = 5000
        this.container = document.getElementById('canvas')

        this.updateStats = new Stats()
        this.updateStats.showPanel(1)
        this.updateStats.domElement.style.position = 'absolute'
        document.getElementById('updateStats').appendChild(this.updateStats.dom)

        this.fpsStats = new Stats()
        this.fpsStats.domElement.style.position = 'absolute'
        document.getElementById('fpsStats').appendChild(this.fpsStats.dom)

        this.clock = new THREE.Clock()

        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color("aliceblue")
        this.scene.add(new THREE.AmbientLight())

        this.camera = new THREE.PerspectiveCamera(75, this.container.clientWidth / this.container.clientHeight, 0.1, 1000);
        // this.camera.position.set(50, 50, 50)

        this.renderer = new THREE.WebGLRenderer({antialiasing: true});
        this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
        this.container.appendChild(this.renderer.domElement);

        this.controls = new OrbitControls(this.camera, this.renderer.domElement);

        const socket = new WebSocket('ws://' + window.location.hostname + ':9090');

        socket.addEventListener('open', function (event) {
            console.log("Websocket connection opened..")
            send(socket, "subscribe")
        });

        socket.addEventListener('close', function (event) {
            console.log("Websocket connection closed..")
        });

        socket.addEventListener('message', function (event) {
            let obj;
            let payload = JSON.parse(event.data)
            const action = payload.action

            switch (action) {
                case "add":
                    that.setup([payload.data])
                    break
                case "remove":
                    obj = that.objects[payload.data]
                    that.scene.remove(obj)
                    break
                case "setup":
                    that.setup(payload.data)
                    const id = setInterval(function () {
                        if (!send(socket, "update")) {
                            clearInterval(id)
                        }
                    }, 33)
                    break
                case "update":
                    that.update(payload.data.transforms)
                    that.updateStats.update()
                    break
                case "visibilityChanged":
                    obj = that.objects[payload.data.name]
                    obj.visible = payload.data.visible
                    break
                case "wireframeChanged":
                    obj = that.objects[payload.data.name]
                    obj.traverse(function (o) {
                        if (o instanceof THREE.Mesh) {
                            o.material.wireframe = payload.data.wireframe
                        }
                    })
                    break
                case "colorChanged":
                    obj = that.objects[payload.data.name]
                    obj.traverse(function (o) {
                        if (o instanceof THREE.Mesh) {
                            o.material.color.setHex(payload.data.color)
                        }
                    })
                    break
            }

        });

        this.updateProjectionMatrix = function () {
            that.camera.aspect = that.container.offsetWidth / that.container.offsetHeight
            that.camera.updateProjectionMatrix()
        }

        let onWindowResize = function () {
            that.updateProjectionMatrix()
            that.renderer.setSize(that.container.offsetWidth, that.container.offsetHeight)
        }

        window.addEventListener('resize', onWindowResize, false);

    }

    setup(config) {

        let that = this
        config.transforms.forEach(function (t) {

            let parent = new THREE.Object3D()
            parent.name = t.name
            if (t.position) {
                parent.position.set(t.position.x, t.position.y, t.position.z)
            }
            if (t.rotation) {
                parent.rotation.set(t.rotation.x, t.rotation.y, t.rotation.z)
            }

            let child = new THREE.Object3D()
            parent.add(child)

            let geometry = t.geometry
            if (geometry) {

                let mat = new THREE.MeshBasicMaterial({
                    color: geometry.color,
                    wireframe: geometry.wireframe
                })
                if (geometry.opacity < 1) {
                    mat.opacity = geometry.opacity
                    mat.transparent = true
                }
                createMesh(geometry.shape, mat, function (mesh) {
                    mesh.matrixAutoUpdate = false
                    if (geometry.offsetPosition) {
                        child.position.set(geometry.offsetPosition.x, geometry.offsetPosition.y, geometry.offsetPosition.z)
                    }
                    if (geometry.offsetRotation) {
                        child.rotation.set(geometry.offsetRotation.x, geometry.offsetRotation.y, geometry.offsetRotation.z)
                    }
                    child.add(mesh)
                })
            }

            if (t.trail) {
                let mat = new THREE.LineBasicMaterial({
                    color: t.trail.color
                })
                parent.userData.trail = {
                    "points": [],
                    "mat": mat,
                    "line": undefined,
                    "maxLength": t.trail.maxLength
                }
                that.trailObjects.push(parent)
            }

            that.scene.add(parent)
            that.objects[t.name] = parent
        })

        if (config.camera) {
            if (config.camera.fov) {
                this.camera.fov = config.camera.fov
            }
            this.camera.position.set(config.camera.position.x, config.camera.position.y, config.camera.position.z)
            if (config.camera.target) {
                this.follow = true
                this.controls.target = this.objects[config.camera.target].position
            }
            this.controls.update()
            this.updateProjectionMatrix()
        }

        if (config.water) {

            const waterGeometry = new THREE.PlaneGeometry(config.water.width, config.water.height);
            this.water = new THREE.Water(
                waterGeometry,
                {
                    textureWidth: 512,
                    textureHeight: 512,
                    waterNormals: new THREE.TextureLoader().load('/assets/waternormals.jpg', function (texture) {
                        texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
                    }),
                    alpha: 1.0,
                    sunDirection: new THREE.Vector3(),
                    sunColor: 0xffffff,
                    waterColor: 0x001e0f,
                    distortionScale: 3.7,
                    fog: this.scene.fog !== undefined
                }
            );

            this.water.rotation.x = -Math.PI / 2;
            this.scene.add(this.water)
        }

        config.transforms.forEach(function (t) {
            if (t.parent) {
                that.objects[t.parent].add(that.objects[t.name])
            }
        })

        this.update(config.transforms)
    }

    update(transforms) {
        let that = this
        transforms.forEach(function (t) {
            let obj = that.objects[t.name]
            if (obj) {
                if (t.position) {
                    obj.position.set(t.position.x, t.position.y, t.position.z)
                }
                if (t.rotation) {
                    obj.rotation.set(t.rotation.x, t.rotation.y, t.rotation.z)
                } else if (t.quaternion) {
                    obj.quaternion.set(t.quaternion.x, t.quaternion.y, t.quaternion.z, t.quaternion.w)
                }
            }
        })
    }

    computeLength(list) {
        if (list.length <= 2) return 0
        let l = 0
        let prev = list[0]
        for (let i = 1; i < list.length; i++) {
            l += prev.distanceTo(list[i])
            prev = list[i]
        }
        return l;
    }

    animate = () => {

        requestAnimationFrame(this.animate);

        if (this.water) {
            this.water.material.uniforms['time'].value += 1.0 / 60.0;
        }

        if (this.follow) {
            this.controls.update()
        }

        const elapsed = this.clock.getElapsedTime()
        if (elapsed > 1.0 / 30) {
            for (let i = 0; i < this.trailObjects.length; i++) {
                const trailObject = this.trailObjects[i]
                const trail = trailObject.userData.trail
                const points = trail.points
                points.push(trailObject.getWorldPosition(new THREE.Vector3()))

                if (points.length < 2) continue
                if (points.length > this.MAX_LINE_POINTS) {
                    points.shift()
                }
                if (trail.maxLength) {
                    let it = 0
                    while (this.computeLength(points) > trail.maxLength && it++ < 100) {
                        points.shift()
                    }
                }

                const geometry = new THREE.BufferGeometry().setFromPoints(points)
                if (trail.line !== undefined) {
                    trail.line.geometry.dispose()
                    trail.line.geometry = geometry
                    geometry.attributes.position.needsUpdate = true
                } else {
                    trail.line = new THREE.Line(geometry, trail.mat)
                    this.scene.add(trail.line)
                }
            }
            this.clock.start()
        }
        this.renderer.render(this.scene, this.camera)
        this.fpsStats.update()

    };

}

export default {
    "typeStr": "Int",
    "gridType": "RookGrid",
    "nrows": 10,
    "ncols": 10,
    "emptyData": 0,
    "cellSize": 50,
    "gridGap": 5,
    "transitionTime": 250,
    "updationFunction": (newInt, oldInt) => newInt,
    "init": (grid) => {
        console.log(grid)
        if(grid.state.now() == 0) {
            grid.functions.placeRandom(grid, 1)
            grid.functions.placeRandom(grid, 2)
            grid.state.set(1)
            grid.variables.movingDirs = [grid.Right]
            if(grid.variables.intervalId)
                clearInterval(grid.variables.intervalId)
            grid.variables.intervalId = setInterval(() => grid.functions.moveSnake(grid), 1000)
            grid.state.set(1)
        }
        console.log("Herehdheheheheh")
    },
    "className": (int) => "C" + (2 * int).toString(),
    "variables": {
        "x": 0,
        "y": 0,
        "movingDirs": [],
        "intervalId": null
    },
    "functions": {
        "placeRandom": function (grid, data) {
            const empties = grid.empties
            if(empties.length > 0) {
                const index = Math.floor(Math.random() * empties.length)
                const [x, y] = empties[index]
                grid.placeByXY(x, y, data)            
            }
        },
        "moveSnake": function (grid) {
            let movingDirs = grid.variables.movingDirs
            console.log("Here 2", movingDirs)
            grid.state.set(2)
            const l = grid.blocks.length
            for (let i = l - 2; i >= 0; i--) {
                const thisBlock = grid.blocks[i]
                const nextSquare = thisBlock.square.neighbour(movingDirs[i])
                if(nextSquare !== undefined){
                    if(nextSquare.block) {
                        if(nextSquare.block.data === 1) {
                            console.log(nextSquare.block, i, movingDirs)
                            alert("YOU MOOOOOROOOOOON, YOU JUST AAAAAAAAAAATE YOURSEEEEEEELF")
                            clearInterval(grid.variables.intervalId)
                        }
                        else {
                            const b = nextSquare.block
                            grid.removeBlock(b)
                            grid.placeBySquare(nextSquare, 1)
                            grid.variables.movingDirs.push(movingDirs[i])
                            grid.functions.placeRandom(grid, 2)
                            break
                        }
                    }
                    thisBlock.moveTo(nextSquare)
                }
                else{
                    clearInterval(grid.variables.intervalId)
                    break
                }
            }
            const last = movingDirs[movingDirs.length - 1]
            movingDirs.shift()
            movingDirs.push(last)
            grid.variables.movingDirs = movingDirs
            grid.state.set(1)
        },
        "turn": function (grid, dir) {
            console.log("Here")
            const int = setInterval(() => {
                if(grid.state.now() == 1){
                    grid.variables.movingDirs.pop()
                    grid.variables.movingDirs.push(dir)
                    clearInterval(int)
                }
            }, 100)
        }
    },
    "blockListeners": {},
    "squareListeners": {},
    "gridListeners": {
        "keydown": function (e) {
            e.stopPropagation()
            e.preventDefault()
            const grid = e.currentTarget.grid
            console.log(e)
            switch (e.key) {
                case "ArrowUp":
                    grid.functions.turn(grid, grid.Up)
                    break
                case "ArrowDown":
                    grid.functions.turn(grid, grid.Down)
                    break
                case "ArrowLeft":
                    grid.functions.turn(grid, grid.Left)
                    break
                case "ArrowRight":
                    grid.functions.turn(grid, grid.Right)
                    break
                default:
                    return
            }
            console.log(grid.dataGrid)
        },
        "touchstart": function (e) {
            if(e.currentTarget != e.target)
                return
            e.stopPropagation()
            e.preventDefault()
            const grid = e.currentTarget.grid
            console.log(e)
            const touch = e.changedTouches[0]
            const x = touch.screenX
            const y = touch.screenY
            grid.variables.x = x
            grid.variables.y = y
        },
        "touchend": function (e) {
            if(e.currentTarget != e.target)
                return
            e.stopPropagation()
            e.preventDefault()
            const grid = e.currentTarget.grid
            console.log(e, grid)
            const touch = e.changedTouches[0]
            const x1 = touch.screenX
            const y1 = touch.screenY
            const x0 = grid.variables.x
            const y0 = grid.variables.y
            const dx = x1 - x0
            const dy = y0 - y1
            const angle = Math.atan2(dy, dx) * 180 / Math.PI
            console.log(dx, dy, angle)
            if(angle >= -45 && angle <= 45)
                grid.functions.turn(grid, grid.Right)
            else if(angle >= 45 && angle <= 135)
                grid.functions.turn(grid, grid.Up)
            else if(angle >= 135 || angle <= -135)
                grid.functions.turn(grid, grid.Left)
            else if(angle >= -135 && angle <= -45)
                grid.functions.turn(grid, grid.Down)

            console.log(grid.dataGrid)

        }
    }
}
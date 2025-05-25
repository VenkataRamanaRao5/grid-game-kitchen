export default {
    "typeStr": "Int",
    "gridType": "RookGrid",
    "nrows": 4,
    "ncols": 4,
    "emptyData": 0,
    "cellSize": 75,
    "gridGap": 5,
    "transitionTime": 500,
    "updationFunction": (newInt, oldInt) => oldInt * 2,
    "init": (grid) => {
        console.log(grid)
        grid.state.signal.forEach(s => {
            switch(s) {
                case 1:
                    grid.blocksVar.update(bV => bV.map(b =>{
                        b.state.set(0)
                        return b
                    }))
                default:
                    return
            }
        })
        grid.functions.placeRandom(grid)
        grid.functions.placeRandom(grid)
        console.log("Here")
    },
    "className": (int) => "C" + int.toString(),
    "variables": {},
    "functions": {
        "placeRandom": function (grid) {
            const empties = grid.empties
            console.log(empties)
            if(empties.length > 0) {
                const index = Math.floor(Math.random() * empties.length)
                const [x, y] = empties[index]
                grid.placeByXY(x, y, Math.random() < 0.9 ? 2 : 4)
            }
        },
        "pullFrom": function (grid, square, dir) {
            const nextSquare = square.nonEmpty(dir)
            if(nextSquare) {
                let next
                let thisBlock = square.block,
                    nextBlock = nextSquare.block
                if(thisBlock && nextBlock) {
                    console.log(dir, square, nextSquare)
                    if(thisBlock.state.now() == 0 && thisBlock.data == nextBlock.data) {
                        grid.removeBlock(thisBlock)
                        nextSquare.moveTo(square)
                        nextBlock.updateData(0)
                        nextBlock.state.set(1)
                        next = square
                    } 
                    else {
                        const temp = square.neighbour(dir)
                        nextSquare.moveTo(temp)
                        next = temp
                    }
                }
                else {
                    nextSquare.moveTo(square)
                    next = square
                }
                grid.functions.pullFrom(grid, next, dir)
            }
        },
        "moveGrid": function (grid, move) {
            grid.state.set(0)
            let frontier, opposite
            switch(move) {
                case grid.Left:
                    frontier = grid.grid.map(e => e[0])
                    opposite = grid.Right
                    break
                case grid.Right:
                    frontier = grid.grid.map(e => e[e.length - 1])
                    opposite = grid.Left
                    break
                case grid.Up:
                    frontier = grid.grid[0]
                    opposite = grid.Down
                    break
                case grid.Down:
                    frontier = grid.grid[grid.grid.length - 1]
                    opposite = grid.Up
                    break
            }
            const oldGrid = grid.dataGrid.flat()
            frontier.forEach(sq => grid.functions.pullFrom(grid, sq, opposite))
            let sameElements = true
            const newGrid = grid.dataGrid.flat()
            for (let i = 0; i < oldGrid.length; i++) {
                console.log(oldGrid[i], newGrid[i])
                if (oldGrid[i] !== newGrid[i]) {
                    sameElements = false
                    break
                }
            }
            console.log(sameElements)
            if(!sameElements)
                grid.functions.placeRandom(grid)
            grid.state.set(1)
            
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
                    grid.functions.moveGrid(grid, grid.Up)
                    break
                case "ArrowDown":
                    grid.functions.moveGrid(grid, grid.Down)
                    break
                case "ArrowLeft":
                    grid.functions.moveGrid(grid, grid.Left)
                    break
                case "ArrowRight":
                    grid.functions.moveGrid(grid, grid.Right)
                    break
                default:
                    return
            }
            console.log(grid.dataGrid)
        }
    }
}
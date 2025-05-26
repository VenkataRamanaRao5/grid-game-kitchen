export default {
    "typeStr": "Int",
    "gridType": "RookGrid",
    "nrows": 2,
    "ncols": 2,
    "emptyData": 0,
    "cellSize": 75,
    "gridGap": 2,
    "transitionTime": 150,
    "updationFunction": (newChar, oldChar) => 1 - oldChar,
    "init": (grid) => {
        let state = grid.state.now()
        console.log(grid, state, grid.state)
        if(state == 0) {
            grid.state.set(2)
            grid.buildGrid(2, 2)
        }
        else {
            grid.buildGrid(state, state)
        }
        console.log(grid)
        grid.clear()
        for(let i = 0; i < state; i++) {
            for(let j = 0; j < state; j++) {
                grid.placeByXY(i, j, 0)
            }
        }
    },
    "className": (char)  => {
        switch(char){
            case 0: return "Blue"
            case 1: return "Yellow"
            case 2: return "Error"
        }
    },
    "variables": {},
    "functions": {
        "flip": function (grid, block) {
            const sq = block.square
            const neighbours = sq.neighbours
            console.log(block, sq, neighbours)
            neighbours.push(sq) // include self
            neighbours.forEach(neighbour => neighbour.block?.updateData(0));
            console.log(grid.dataGrid)
            if(grid.functions.checkWin(grid)){
                console.log("Passed")
                grid.functions.nextLevel(grid)
            }
        },
        "checkWin": function (grid) {
            return grid.dataGrid.flat().every(data => data == 1)
        },
        "nextLevel": function (grid) {
            let state = grid.state.now()
            if(state == 5) {
                alert("You have completed the game!")
                return
            }
            console.log(state, grid.state)
            state += 1
            grid.state.set(state)
            console.log(state, grid.state)
            setTimeout(() => grid.init(grid), 500)
        },
    },
    "blockListeners": {
        "click": function (e) {
            e.stopPropagation()
            e.preventDefault()
            const grid = e.target.grid
            const block = e.target.block
            grid.functions.flip(grid, block)
        }
    },
    "squareListeners": {
        "click": function (e) {
            e.stopPropagation()
            e.preventDefault()
            console.log("You clicked on a square:", e.target, e.target.square)
            const grid = e.target.grid
            const square = e.target.square
            grid.functions.ply(grid, square)
        }
    },
    "gridListeners": {
    }
}
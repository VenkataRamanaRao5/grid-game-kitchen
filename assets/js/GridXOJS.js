export default {
    "typeStr": "Char",
    "gridType": "QueenGrid",
    "nrows": 3,
    "ncols": 3,
    "emptyData": ' ',
    "cellSize": 75,
    "gridGap": 5,
    "transitionTime": 5,
    "updationFunction": (newChar, oldChar) => newChar,
    "init": (grid) => {
        console.log(grid)
        grid.variables.turn = 0
        grid.clear()
    },
    "className": (char)  => char.toString() ,
    "variables": {
        "turn": 0,
    },
    "functions": {
        "checkWinAt": function (grid, square) {
            const ch = square.block.data
            return square.thisRow.every(e1 => e1?.block?.data == ch) ||
            square.thisColumn.every(e1 => e1?.block?.data == ch) ||
            ((square.row == square.col) && square.backSlashDiagonal.every(e1 => e1?.block?.data == ch)) ||
            ((square.row + square.col == grid.nrows - 1)) && square.forwardSlashDiagonal.every(e1 => e1?.block?.data == ch)
        },
        "ply": function (grid, square) {
            if(grid.state != 0) {
                alert("Game over!")
                return
            }
            const ch = grid.variables.turn == 0 ? "X" : "O"
            grid.placeBySquare(square, ch)
            let res = grid.functions.checkWinAt(grid, square)
            if(res) {
                setTimeout(() => alert(`Player ${ch} wins!`), 100)
                grid.state = 1
            }
            grid.variables.turn = 1 - grid.variables.turn
        }
    },
    "blockListeners": {
        "click": function (e) {
            e.stopPropagation()
            e.preventDefault()
            alert("You clicked on a block")
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
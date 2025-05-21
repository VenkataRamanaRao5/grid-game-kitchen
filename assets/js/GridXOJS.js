export const GridXO = {
    "typeStr": "char",
    "gridType": "QueenGrid",
    "nrows": 3,
    "ncols": 3,
    "emptyData": " ",
    "updationFunction": (newChar, oldChar) => newChar,
    "init": (grid) => {
        console.log(grid)
        grid.clear()
    },
    "className": (char) => char.toString(),
    "variables": {
        "turn": 0,
    },
    "functions": {
        "checkWinAt": function (grid, square) {
            console.log("Checking win at", square)
            const ch = square.block.get__O().data
            return square.thisRow.forall(e1 => e1.block.forall(e2 => e2.data == ch)) ||
            square.thisColumn.forall(e1 => e1.block.forall(e2 => e2.data == ch)) ||
            square.backSlashDiagonal.forall(e1 => e1.block.forall(e2 => e2.data == ch)) ||
            square.forwardSlashDiagonal.forall(e1 => e1.block.forall(e2 => e2.data == ch))
        },
        "ply": function (grid, square) {
            const ch = grid.variables.turn == 0 ? "X" : "O"
            grid.placeBySquare(square, ch)
            console.log("Placed", ch, "at", square)
            grid.functions.checkWinAt(grid, square)
            grid.variables.turn = 1 - grid.variables.turn
        }
    },
    "blockListeners": {
        "click": function (e) {
            alert("You clicked on a block")
        }
    },
    "squareListeners": {
        "click": function (e) {
            console.log("You clicked on a square:", e.target, e.target.square)
            const grid = e.target.grid
            const square = e.target.square
            grid.functions.ply(grid, square)
        }
    },
    "gridListeners": {
    }
}
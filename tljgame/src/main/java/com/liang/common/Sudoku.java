package com.liang.common;

public class Sudoku {

  private int[][] matrix;

  public Sudoku(int[][] matrix) {
    this.matrix = matrix;
  }

  public static void main(String[] args) {
    // 号称世界上最难数独
    int[][] sudoku = {
        {0, 9, 5, 0, 6, 0, 0, 0, 0},
        {7, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 4},
        {0, 7, 0, 0, 0, 8, 4, 1, 0},
        {1, 0, 9, 2, 0, 4, 0, 6, 3},
        {2, 3, 0, 0, 0, 0, 9, 5, 0},
        {4, 5, 7, 3, 8, 6, 0, 2, 0},
        {9, 6, 1, 0, 4, 2, 8, 0, 0},
        {3, 2, 0, 0, 9, 7, 6, 0, 5}};
    Sudoku s = new Sudoku(sudoku);
    s.backTrace(0, 0);
  }

  /**
   * 数独算法
   *
   * @param i 行号
   * @param j 列号
   */
  private void backTrace(int i, int j) {
    if (i == 8 && j == 9) {
      //已经成功了，打印数组即可
      System.out.println("获取正确解");
      printArray();
      return;
    }

    //已经到了列末尾了，还没到行尾，就换行
    if (j == 9) {
      i++;
      j = 0;
    }

    //如果i行j列是空格，那么才进入给空格填值的逻辑
    if (matrix[i][j] == 0) {
      for (int k = 1; k <= 9; k++) {
        //判断给i行j列放1-9中的任意一个数是否能满足规则
        if (check(i, j, k)) {
          //将该值赋给该空格，然后进入下一个空格
          matrix[i][j] = k;
          backTrace(i, j + 1);
          //初始化该空格
          matrix[i][j] = 0;
        }
      }
    } else {
      //如果该位置已经有值了，就进入下一个空格进行计算
      backTrace(i, j + 1);
    }
  }

  /**
   * 判断给某行某列赋值是否符合规则
   *
   * @param row 被赋值的行号
   * @param line 被赋值的列号
   * @param number 赋的值
   */
  private boolean check(int row, int line, int number) {
    //判断该行该列是否有重复数字
    for (int i = 0; i < 9; i++) {
      if (matrix[row][i] == number || matrix[i][line] == number) {
        return false;
      }
    }
    //判断小九宫格是否有重复
    int tempRow = row / 3;
    int tempLine = line / 3;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (matrix[tempRow * 3 + i][tempLine * 3 + j] == number) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * 打印矩阵
   */
  public void printArray() {
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
    System.out.println();
  }

}

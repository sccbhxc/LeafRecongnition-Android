package com.example.tab4;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class Curvature {
	Mat bw = new Mat();
	Point[] contourArr = null;
	double[] curArr = null;
	double[] removeCur = null;
	final double PI = 3.14159265358979;
	private int handleIndex;
	private int handleLength;
	int[] removeContourIndex = null;
	final int barNum = 21;
	double[] Hist = new double[barNum];
	double[] meanCurArr;

	public Curvature(Mat Bw, Point[] betterArr) {
		bw = Bw.clone();
		contourArr = betterArr;
		handleIndex=0;
		handleLength=0;
	}

	// 修正后的像素面积
	public double squarePartArea(double deltaI, double deltaJ, double r) {
		double alpha, beta, gama, theta, omiga, s1, s2, s3, s4, sTotal, sH;
		deltaI = Math.abs(deltaI);
		deltaJ = Math.abs(deltaJ);
		double d = deltaI + deltaJ;
		double Delta[] = { deltaI - 0.5, deltaI + 0.5, deltaJ - 0.5,
				deltaJ + 0.5 };
		int delta = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 2; j < 4; j++) {
				// 判断正方形有几个角进入圆内
				if (Delta[i] * Delta[i] + Delta[j] * Delta[j] < r * r)
					delta++;
			}
		}
		if (deltaI != 0 && deltaJ != 0) {
			switch (delta) {
			case 0:
				return 0;
			case 1:
				alpha = Math.asin(Delta[2] / r);
				beta = Math.asin(Delta[0] / r);
				theta = 0.5 * PI - alpha - beta;
				sTotal = 0.5 * theta * r * r;
				s1 = 0.5 * (Math.sqrt(r * r - Delta[2] * Delta[2]) - Delta[0])
						* Delta[2];
				s2 = 0.5 * (Math.sqrt(r * r - Delta[0] * Delta[0]) - Delta[2])
						* Delta[0];
				return sTotal - s1 - s2;
			case 2:
				if (deltaI >= deltaJ) {
					alpha = Math.asin(Delta[2] / r);
					beta = Math.atan(Delta[0] / Delta[3]);
					theta = 0.5 * PI - alpha - beta;
					gama = Math.acos(Delta[3] / r)
							- Math.atan(Delta[0] / Delta[3]);
					sTotal = 0.5 * theta * r * r;
					s1 = 0.5
							* (Math.sqrt(r * r - Delta[2] * Delta[2]) - Delta[0])
							* Delta[2];
					s2 = 0.5 * Delta[0];
					s3 = 0.5
							* gama
							* r
							* r
							- 0.5
							* (Math.sqrt(r * r - Delta[3] * Delta[3]) - Delta[0])
							* Delta[3];
					return sTotal - s1 - s2 - s3;
				} else {
					alpha = Math.atan(Delta[2] / Delta[1]);
					beta = Math.asin(Delta[0] / r);
					theta = 0.5 * PI - alpha - beta;
					gama = Math.atan(Delta[1] / Delta[2])
							- Math.asin(Delta[1] / r);
					sTotal = 0.5 * theta * r * r;
					s1 = 0.5 * Delta[2];
					s2 = 0.5
							* (Math.sqrt(r * r - Delta[0] * Delta[0]) - Delta[2])
							* Delta[0];
					s3 = 0.5
							* gama
							* r
							* r
							- 0.5
							* (Math.sqrt(r * r - Delta[1] * Delta[1]) - Delta[2])
							* Delta[1];
					return sTotal - s1 - s2 - s3;
				}

			case 3:
				alpha = Math.atan(Delta[2] / Delta[1]);
				beta = Math.atan(Delta[0] / Delta[3]);
				theta = 0.5 * PI - alpha - beta;
				gama = Math.acos(Delta[3] / r) - Math.atan(Delta[0] / Delta[3]);
				omiga = Math.acos(Delta[1] / r)
						- Math.atan(Delta[2] / Delta[1]);
				sTotal = 0.5 * theta * r * r;
				s1 = 0.5 * Delta[2];
				s2 = 0.5 * Delta[0];
				s3 = 0.5 * gama * r * r - 0.5
						* (Math.sqrt(r * r - Delta[3] * Delta[3]) - Delta[0])
						* Delta[3];
				s4 = 0.5 * omiga * r * r - 0.5
						* (Math.sqrt(r * r - Delta[1] * Delta[1]) - Delta[2])
						* Delta[1];
				return sTotal - s1 - s2 - s3 - s4;
			case 4:
				return 1;
			default:
				System.out.println("像素点出错");
				return 0;
			}
		} else if (delta == 2) {
			alpha = Math.asin(0.5 / r);
			theta = Math.acos((d - 0.5) / r);
			beta = theta - alpha;
			sTotal = theta * r * r;
			sH = 0.5 * beta * r * r;
			s3 = (d - 0.5) * Math.sqrt(r * r - (d - 0.5) * (d - 0.5));
			s1 = 0.5 * (0.5 - (d - 0.5) * Math.tan(alpha))
					* (0.5 - (d - 0.5) * Math.tan(alpha)) / Math.tan(alpha);
			s2 = 0.5
					* (Math.sqrt(r * r - (d - 0.5) * (d - 0.5)) - (d - 0.5)
							* Math.tan(alpha)) * (d - 0.5);
			return sTotal - 2 * (sH - s1 - s2) - s3;
		} else
			return 1;
	}

	// 计算曲率
	public void getCur(int radius) {
		int j = 0, count = 0;
		int curLength = contourArr.length;
		double circleArea = PI * radius * radius;
		List<Double> cur = new ArrayList<Double>();
		while (j < curLength) {
			double temp1 = 0;
			cur.add(0.0);
			int temp_y = (int) contourArr[j].y;
			int temp_x = (int) contourArr[j].x;
			if (temp_y - radius < 0 || temp_y + radius >=bw.rows()
					|| temp_x - radius < 0 || temp_x + radius >=bw.cols()) {
				double temp = 0;
				for (int i = count - 8; i < count; i++) {
					temp += cur.get(i) / 8;
				}
				cur.set(count, temp);
			} else {
				for (int k = temp_y - radius; k <= temp_y + radius; k++) {
					for (int kk = temp_x - radius; kk <= temp_x + radius; kk++) {
						if ((int) bw.get(k, kk)[0] == 0) {
							temp1 += squarePartArea(k - temp_y, kk - temp_x,
									radius);
						}
//						if ((int) bw.get(k, kk)[0] == 0&&(k-temp_y)*(k-temp_y)
//								+(kk-temp_x)*(kk-temp_x)<radius*radius)temp1+=1;
					}
				}
				cur.set(count, temp1);
			}
			count++;
			j += 6;
		}
		// 将list转化为double[]
		int sampleNum = cur.size();
		Double[] CurArr = new Double[sampleNum];
		cur.toArray(CurArr);
		curArr = new double[sampleNum];
		for (int i = 0; i < sampleNum; i++) {
			curArr[i] = CurArr[i];
		}
		// 求比值
		for (int i = 0; i < sampleNum; i++) {
			curArr[i] /= circleArea;
			//System.out.println(i + "曲率：" + curArr[i]);
		}
		//System.out.println("缩轮廓长："+sampleNum);
	}

	// 求平均曲率
	private void getMeanCur(double[] meanCur) {
		int sampleNum = curArr.length;
		int range = 10;
		int num = 2 * range + 1;
		for (int i = 0; i < range; i++) {
			for (int j = sampleNum - range + i; j < sampleNum; j++) {
				meanCur[i] += curArr[j] / num;
			}
			for (int j = 0; j < i + range; j++) {
				meanCur[i] += curArr[j] / num;
			}
		}
		for (int i = range; i < sampleNum - range; i++) {
			for (int j = i - range; j < i + range; j++) {
				meanCur[i] += curArr[j] / num;
			}
		}
		for (int i = sampleNum - range; i < sampleNum; i++) {
			for (int j = i - range + 1; j < sampleNum; j++) {
				meanCur[i] += curArr[j] / num;
			}
			for (int j = 0; j < range - sampleNum + i; j++) {
				meanCur[i] += curArr[j] / num;
			}
		}
	}

	// 寻找叶柄,求最大值的下标
	private int findMax(double[] meanCur) {
		int sampleNum = meanCur.length;
		double maxCur = 0;
		int indexOfMax = 0;
		for (int i = 0; i < sampleNum; i++) {
			if (meanCur[i] > maxCur) {
				maxCur = meanCur[i];
				indexOfMax = i;
			}
		}
		return indexOfMax;
	}

	// 确定最大长度
	private int findMax(int[] curLength) {
		int sampleNum = curLength.length;
		int maxLength = 0;
		int indexOfMax = 0;
		for (int i = 0; i < sampleNum; i++) {
			if (curLength[i] > maxLength) {
				maxLength = curLength[i];
				indexOfMax = i;
			}
		}
		return indexOfMax;
	}

	// 确定叶柄长度
	private int getHandleLength(double[] cur) {
		int sampleNum = cur.length;

		int[] CurLength = new int[sampleNum];
		for (int i = 0; i < CurLength.length; i++) {
			CurLength[i] = 0;
		}

		int mark = 0;
		int count = 0;
		double meanOfCur = 0;
		for (int i = 0; i < sampleNum; i++) {
			meanOfCur = cur[i] / sampleNum;
		}

		double thresh = (meanOfCur > 0.5) ? meanOfCur : 0.5;// 取平均曲率与0.5中较大者作为阈值

		int i = 0, j;
		// 确保图片首位能够连续
		while (i < 2 * sampleNum) {
			if (i < sampleNum) {
				j = i;
				if (cur[j] > thresh) {
					CurLength[count]++;
					mark = 0;
				} else if (mark == 0) {
					count++;
					mark = 1;
				}
				j++;
			} else {
				j = i - sampleNum;
				if (cur[j] > thresh) {
					CurLength[count]++;
					mark = 0;
				} else if (mark == 0) {
					count++;
					mark = 1;
				}
			}
			i++;
		}
		int handleLength = CurLength[findMax(CurLength)];
		return (int) (handleLength / 2);
	}

	// 求最小值
	private int findMinDouble(double[] a, int n) {
		double tmp = a[0];
		int minIndex = 0;
		for (int i = 0; i < n; i++) {
			if (tmp > a[i]) {
				tmp = a[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	// 确定叶柄位置
	private int getHandlePos(int handleLength, double[] meanCur) {
		int distLength = 2 * handleLength;
		int distNum = 2 * handleLength + 1;// 要求距离的个数
		int curLength = curArr.length;
		int handleIndex = findMax(meanCur);
		double[] dist = new double[distNum];
		int[] centerPoint = new int[distNum];
		for (int i = 0; i < distNum; i++) {
			dist[i] = 0;
		}
		for (int i = 0; i < distNum; i++) {
			centerPoint[i] = (handleIndex - handleLength + i + curLength)
					% curLength;
			for (int j = 0; j < distLength; j++) {
				dist[i] += Math
						.abs(meanCur[(centerPoint[i] - distLength + j + curLength)
								% curLength]
								- meanCur[(centerPoint[i] + distLength - j + curLength)
										% curLength]);
			}
		}
		int position;// 叶柄头位置
		int tmp;
		tmp = findMinDouble(dist, distNum);
		position = centerPoint[tmp];
		// System.out.println("叶柄位置"+position);
		return position;
	}

	// 在叶柄大于一定长度以后，除去叶柄后的曲率
	public void removeHandleCur() {
		int sampleNum = curArr.length;
		meanCurArr = new double[sampleNum];
		getMeanCur(meanCurArr);
		int handleLenthByC = getHandleLength(curArr);
		int handleLenthByM = getHandleLength(meanCurArr);

		// 确定合适叶柄长度
		if ((double) handleLenthByM / (double) handleLenthByC > 1.5) {
			handleLength = handleLenthByM;
		} else {
			handleLength = handleLenthByC;
		}

		// 长度大于阈值8，则认为是柄，否则认为无叶柄
		if (handleLength > 8&&PicTabActivity.isEidt==false) {
		//if (handleLength > 8) {
			// 获取准确叶柄头位置
			//System.out.println("去除了叶柄");
			handleIndex = getHandlePos(handleLength, meanCurArr);
			int removeCurLength = sampleNum - (2 * handleLength + 1);// 非叶柄部分长度
			removeCur = new double[removeCurLength];

			//System.out.println("去除叶柄后轮廓长：" + 6 * removeCurLength);
			for (int i = 0; i < removeCurLength; i++) {
				int tmp = (handleIndex + handleLength + 1 + i) % sampleNum;
				removeCur[i] = curArr[tmp];
				//System.out.println(i + "去除叶柄后曲率：" + removeCur[i]);
			}
		} else {
			removeCur = new double[sampleNum];
			for (int i = 0; i < sampleNum; i++) {
				removeCur[i] = curArr[i];
			}
		}
	}

	// 去除叶柄后轮廓下标
	public void removeHandleIndex() {
		int sampleNum = curArr.length;

		if (handleLength > 8&&PicTabActivity.isEidt==false) {
		//if (handleLength > 8) {
			int removeCurLength = sampleNum - (2 * handleLength + 1);// 非叶柄部分长度
			removeContourIndex = new int[removeCurLength];
			int[] tmpIndex = new int[removeCurLength];// 记录非叶柄部分下标
			for (int i = 0; i < removeCurLength; i++) {
				int tmp = (handleIndex + handleLength + 1 + i) % sampleNum;
				tmpIndex[i] = tmp;
			}
			if (handleIndex + handleLength >= removeCurLength
					|| handleIndex - handleLength < 0) {
				for (int i = 0; i < removeCurLength; i++) {
					removeContourIndex[i] = 6 * tmpIndex[i];
				}

			} else {
				for (int i = 0; i < removeCurLength; i++) {
					int tmp = (handleIndex - handleLength + i)
							% removeCurLength;
					removeContourIndex[i] = 6 * tmpIndex[tmp];
				}
			}
		} else {
			removeContourIndex = new int[sampleNum];
			for (int i = 0; i < sampleNum; i++) {
				removeContourIndex[i] = 6 * i;
			}
		}
	}

	// 求直方图
	public void getHist() {
		// 求曲率直方图
		for (int i = 0; i < barNum; i++) {
			Hist[i] = 0;
		}
		int sampleNum = removeCur.length;
		
		for (int i = 0; i < sampleNum; i++) {
			Double temp=Double.valueOf(removeCur[i] * barNum);
			Hist[temp.intValue()]++;
		}
		
		// 归一化
		for (int i = 0; i < barNum; i++) {
			Hist[i] /=  sampleNum;
		}
	}

	//释放内存
	public void releaseMemory(){
		bw.release();
	}
	
	//曲率输出
 	void outputCur(double[] a,String filename) throws IOException{
		 File file = new File("/storage/extSdCard/"+filename);  //存放数组数据的文件
		 
		  FileWriter out = new FileWriter(file);  //文件写入流
		 int n=a.length;
		  //将数组中的数据写入到文件中。每行各数据之间空格间隔
		  for(int i=0;i<n;i++){
		    out.write(a[i]+" ");
		  }
		  out.close();
	}

 	//将曲率数组传到AnalysisTabActivity中
 	void sendCurToAn(){
 		AnalysisTabActivity.curArr=curArr;
 		AnalysisTabActivity.meanCurArr=meanCurArr;
 		AnalysisTabActivity.handleIndex=handleIndex;
 		AnalysisTabActivity.handleLength=handleLength;
 	}
}

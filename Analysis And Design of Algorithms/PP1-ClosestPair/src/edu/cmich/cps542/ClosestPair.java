package edu.cmich.cps542;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

/**
 * Class for finding closest pairs.
 * 
 * @author gadda1k, sabha1c, boyin1s, kadiy2s
 * @since 1-30-2022
 */
public class ClosestPair {
	// Constant for holding the points.txt file path. Update this if 
	// you need to change to some new path.
	public static final String POINTS_PATH = "e:\\Analysis And Design of Algorithms\\PP1-ClosestPair\\testpoints.txt";
	public static void main(String[] args) {
		try {
			// Initialize BufferedReader
			BufferedReader reader = new BufferedReader(
					new FileReader(POINTS_PATH));
			
			// Loop over the reader and parse the points from the file.
			String line;
			ArrayList<Point> points = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				points.add(getPointFromLine(line));
			}

			// Close reader.
			reader.close();

			// Sort points ordered by X and Y coordinates and save them in two arrays.
			ArrayList<Point> pointsXOrdered = sort(points, true);
			ArrayList<Point> pointsYOrdered = sort(points, false);

			// Call the efficientClosestPair method and print the output.
			PointPair closestPointPair = efficientClosestPair(pointsXOrdered, pointsYOrdered);
			System.out.println(closestPointPair);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/** 
	 * @param pointsXOrdered Points ordered by X coordinates.
	 * @param pointsYOrdered Points ordered by Y coordinates.
	 * @return PointPair The two closest points.
	 */
	public static PointPair efficientClosestPair(ArrayList<Point> pointsXOrdered, ArrayList<Point> pointsYOrdered) {
		// Get size of points ordered by X coordinate.
		int n = pointsXOrdered.size();

		// Do brute force approach if n <= 3.
		if (n <= 3) {
			return bruteClosestPair(pointsXOrdered);
		}

		// Find mid index.
		int mid = n / 2;

		// Create empty arrays to store points lying in left and right halves.
		ArrayList<Point> pL = new ArrayList<>();
		ArrayList<Point> qL = new ArrayList<>();
		ArrayList<Point> pR = new ArrayList<>();
		ArrayList<Point> qR = new ArrayList<>();

		// Iterate the first half and add points to left arrays (pL and qL).
		for (int i = 0; i < mid; i++) {
			pL.add(pointsXOrdered.get(i));
		}

		for (int i = 0; i < mid; i++) {
			qL.add(pointsYOrdered.get(i));
		}

		// Iterate the second half and add points to right arrays (pR and qR)
		for (int i = mid; i < n; i++) {
			pR.add(pointsXOrdered.get(i));
		}

		for (int i = mid; i < n; i++) {
			qR.add(pointsYOrdered.get(i));
		}

		// Recursively find the closest pair in left and right halves.
		PointPair dL = efficientClosestPair(pL, qL);
		PointPair dR = efficientClosestPair(pR, qR);

		// Get distances of closest pairs on both halves.
		double dlDistance = dL.distBetweenPoints();
		double drDistance = dR.distBetweenPoints();

		// Get the shortest of both.
		double shortestDistanceSoFar = Math.min(dlDistance, drDistance);

		// Calculate it's distance.
		double dMinSqr = Math.pow(shortestDistanceSoFar, 2);

		// Get the x coordinate mid point from points ordered by X.
		double xOfMidPoint = pointsXOrdered.get((int) (Math.ceil(mid) - 1)).x;
		
		// Assign [minPair] with the two closest points.
		PointPair minPair;
		if (dlDistance == shortestDistanceSoFar) {
			minPair = dL;
		} else {
			minPair = dR;
		}

		// Just in case, the closest pair is splitted, store the points 
		// in [splittedPoints].
		ArrayList<Point> splittedPoints = new ArrayList<>();

		// Loop over points ordered by Y coordinates and consider only those points
		// where |x - xOfMidPoint| < shortestDistanceSoFar.
		for (int i = 0; i < n; i++) {
			if (Math.abs(pointsYOrdered.get(i).x - xOfMidPoint) < shortestDistanceSoFar) {
				splittedPoints.add(pointsYOrdered.get(i));
			}
		}

		// Store the size of [splittedPoints] in [num].
		int num = splittedPoints.size();

		// Iterate over [splittedPoints] and check if there's any pair that's even shorter than dMinSqr. 
		// If so, update the [minPair] with the new pair.
		for (int i = 0; i < num - 2; i++) {
			int k = i + 1;
			while (k <= num - 1 && Math.pow((splittedPoints.get(i).y - splittedPoints.get(k).y), 2) < dMinSqr) {
				minPair = new PointPair(splittedPoints.get(k), splittedPoints.get(i));
				dMinSqr = Math.min(dMinSqr, minPair.distSqrdBetweenPoints());
				k++;
			}
		}

		// Return [minPair].
		return minPair;

	}

	
	/** 
	 * @param points Points in the Cartesian plane.
	 * @return PointPair The two closest points.
	 */
	public static PointPair bruteClosestPair(ArrayList<Point> points) {
		// Assume closestPointPair to be first two points.
		PointPair pointPair = new PointPair(points.get(0), points.get(1));
		PointPair nearestPointPair = pointPair;

		// Initialize distance with first two points.
		double distance = pointPair.distBetweenPoints();

		// Iterate over all the points by fixing one point and keep
		// updating the [distance] and [nearestPointPair] if applicable.
		for (int i = 0; i < points.size(); i++) {
			for (int j = i + 1; j < points.size(); j++) {
				PointPair currPointPair = new PointPair(points.get(i), points.get(j));
				double currentDistance = currPointPair.distBetweenPoints();
				if (currentDistance < distance) {
					distance = currentDistance;
					nearestPointPair = currPointPair;
				}
			}
		}

		// Return the nearestPointPair.
		return nearestPointPair;

	}

	
	/** 
	 * @param points The points to be sorted.
	 * @param sortXCoordinates If true, points are sorted by X coordinates, Y coordinates otherwise.
	 * @return ArrayList<Point> Sorted points ordered on [sortXCoordinates].
	 */
	public static ArrayList<Point> sort(ArrayList<Point> points, boolean sortXCoordinates) {
		// Get total points.
		int n = points.size();
		
		// Create new sortedPoints array.
		ArrayList<Point> sortedPoints = new ArrayList<>();

		// Add all sorted points ordered by [sortXCoordinates] to [sortedPoints].
		sortedPoints.addAll(mergeSort(points, 0, n - 1, sortXCoordinates));

		// Return sortedPoints.
		return sortedPoints;
	}

	
	/** 
	 * @param points The points to be sorted.
	 * @param l First index.
	 * @param r Last index.
	 * @param sortXCoordinates If true, points are sorted by X coordinates, Y coordinates otherwise.
	 * @return ArrayList<Point> Sorted points based on [sortXCoordinates].
	 */
	public static ArrayList<Point> mergeSort(ArrayList<Point> points, int l, int r, boolean sortXCoordinates) {
		if (l < r) {
			int m = l + (r - l) / 2;

			// Split the points recursively by [0..m] and [m + 1, r].
			mergeSort(points, l, m, sortXCoordinates);
			mergeSort(points, m + 1, r, sortXCoordinates);

			// Merge the points recursively after sorting.
			merge(points, l, m, r, sortXCoordinates);
		}
		return points;
	}

	
	/** 
	 * @param points The points array to be merged.
	 * @param l First index.
	 * @param m Mid index.
	 * @param r Last index.
	 * @param sortXCoordinates If true, points are sorted by X coordinates, Y coordinates otherwise.
	 */
	public static void merge(ArrayList<Point> points, int l, int m, int r, boolean sortXCoordinates) {
		// Sizes of left and right sub arrays.
		int p = m - l + 1;
		int q = r - m;

		// Create two temporary arrays.
		ArrayList<Point> pointsL = new ArrayList<>();
		ArrayList<Point> pointsR = new ArrayList<>();

		// Add first half values to pointsL array.
		for (int i = 0; i < p; ++i)
			pointsL.add(points.get(l + i));

		// Add remaining values to pointR array.
		for (int j = 0; j < q; ++j)
			pointsR.add(points.get(m + 1 + j));

		// Initial indices.
		int i = 0;
		int j = 0;
		
		// Initial index of merged sub array.
		int k = l;
		while (i < p && j < q) {
			double a, b;

			// Get a and b based on [sortXCoordinates].
			if (sortXCoordinates) {
				a = pointsL.get(i).x;
				b = pointsR.get(j).x;
			} else {
				a = pointsL.get(i).y;
				b = pointsR.get(j).y;
			}

			if (a <= b) {
				points.set(k, pointsL.get(i));
				i++;
			} else {
				points.set(k, pointsR.get(j));
				j++;
			}
			k++;
		}

		// Copy remaining elements of pointsL if any.
		while (i < p) {
			points.set(k, pointsL.get(i));
			i++;
			k++;
		}

		// Copy remaining elements of pointsR if any.
		while (j < q) {
			points.set(k, pointsR.get(j));
			j++;
			k++;
		}
	}

	
	/** 
	 * @param line The line read by BufferedReader.
	 * @return Point Parsed point from the [line].
	 */
	public static Point getPointFromLine(String line) {
		// Compile regex into a pattern.
		Pattern pattern = Pattern.compile("[+-]?([0-9]*[.])?[0-9]+");
		// Setup matcher on [line].
		Matcher matcher = pattern.matcher(line);
		// Find patterns and group them.
		matcher.find();
		String x = matcher.group();
		matcher.find();
		String y = matcher.group();

		// Return parsed x and y coordinates as a [Point]
		return new Point(Double.parseDouble(x), Double.parseDouble(y));
	}
}

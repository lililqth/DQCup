package dqcup.repair.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import dqcup.repair.RepairedCell;

public class TestUtil {
	public static Set<RepairedCell> readTruth(String fileRoute) {
		File file = new File(fileRoute);
		Set<RepairedCell> truth = new HashSet<RepairedCell>();

		if (!file.exists()) {
			System.out.println(fileRoute + "文件不存在");
			return truth;
		}
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			while (null != (line = br.readLine())) {
				String[] paras = line.split(",");
				RepairedCell cell = null;
				if (paras.length == 2) {
					cell = new RepairedCell(Integer.parseInt(paras[0]), paras[1], "");
				} else {
					cell = new RepairedCell(Integer.parseInt(paras[0]), paras[1], paras[2]);
				}
				truth.add(cell);
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return truth;
	}
}

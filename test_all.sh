#!/bin/bash
echo "Testing input_points1.txt with alpha 1.5 against output_points1_alpha1.5.txt"
java -cp bin EMST data/input_points1.txt 1.5 > out1.txt
diff -u data/output_points1_alpha1.5.txt out1.txt

echo "Testing input_points2.txt with alpha 1.5 against output_points2_alpha1.5.txt"
java -cp bin EMST data/input_points2.txt 1.5 > out2.txt
diff -u data/output_points2_alpha1.5.txt out2.txt

echo "Testing input_points3.txt with alpha 1.1 against output_points3_alpha1.1.txt"
java -cp bin EMST data/input_points3.txt 1.1 > out3.txt
diff -u data/output_points3_alpha1.1.txt out3.txt

echo "Testing input_points4.txt with alpha 1.1 against output_points4_alpha1.1.txt"
java -cp bin EMST data/input_points4.txt 1.1 > out4.txt
diff -u data/output_points4_alpha1.1.txt out4.txt

echo "Testing input_points5.txt with alpha 2.0 against output_points5_alpha2.txt"
java -cp bin EMST data/input_points5.txt 2.0 > out5.txt
diff -u data/output_points5_alpha2.txt out5.txt

echo "Testing input_points6.txt with alpha 1.5 against output_points6_alpha1.5.txt"
java -cp bin EMST data/input_points6.txt 1.5 > out6.txt
diff -u data/output_points6_alpha1.5.txt out6.txt

echo "Done."

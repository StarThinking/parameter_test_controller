# create start table
component=NameNode; for i in org.*; do starts=$(~/parameter_test_controller/test_for_component/hdfs/start_analysis.sh $i $component); if [ $starts -ge 1 ]; then parameter=$(echo $i | awk -F '-output.txt' '{print $1}'); echo $parameter' '$starts; fi; done > "$component"_Start.txt

# create restart table
component=NameNode; for i in org.*; do if [ $(~/parameter_test_controller/test_for_component/hdfs/restart_analysis.sh $i $component) -eq 1 ]; then parameter=$(echo $i | awk -F '-output.txt' '{print $1}'); echo -n $parameter' '; ~/parameter_test_controller/test_for_component/hdfs/start_analysis.sh $i $component; fi; done > "$component"_Restart.txt

# create instace_ge_2 table
component=NameNode; for i in *; do starts=$(~/parameter_test_controller/test_for_component/hdfs/start_analysis.sh $i $component); if [ $starts -ge 2 ]; then echo $i' '$starts; fi; done

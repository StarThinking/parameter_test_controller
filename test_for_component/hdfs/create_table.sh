# create start table
component=NameNode; for i in *; do starts=$(../start_analysis.sh $i $component); if [ $starts -ge 1 ]; then echo $i' '$starts; fi; done

# create restart table
component=NameNode; for i in *; do if [ $(../restart_analysis.sh $i $component) -eq 1 ]; then echo -n $i' '; ../start_analysis.sh $i $component; fi; done

# create instace_ge_2 table
component=NameNode; for i in *; do starts=$(../start_analysis.sh $i $component); if [ $starts -ge 2 ]; then echo $i' '$starts; fi; done

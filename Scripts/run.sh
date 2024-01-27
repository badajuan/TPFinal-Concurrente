#!/bin/bash

# Numero de iteraciones
ITERATIONS=1

# Parsea cantidad de operaciones
while getopts ":n:" opt; do
  case $opt in
    n)
      ITERATIONS=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

shift $((OPTIND - 1))

if [ -z "$1" ]; then
  echo "Uso: $0 -n <iteraciones> <program_path>"
  exit 1
fi

# Path del programa
RUN_PATH=$1

shift

# Argumentos del programa
PROGRAM_ARGS=$@

# Bucle de ejecucion
for (( i=1; i<=$ITERATIONS; i++ ));
do  
  java -jar $RUN_PATH $PROGRAM_ARGS
done


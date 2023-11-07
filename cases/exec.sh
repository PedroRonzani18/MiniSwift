#!/bin/bash

if [ $# -lt 2 ]; then
    echo "Usage: $0 [Dir] [Command ...]";
    exit 1;
fi

dir=$1;
shift;
cmd=$@;

base="$(dirname $0)";
[[ "${base}" =~ ^\/ ]] || base="$(pwd)/${base}";

for n in {0{1..9},{10..15}}; do
    source=${base}/case${n}-???.mswift;
    input=${base}/case${n}-???.in;
    output=${base}/case${n}-???.out;
    result="result${n}.out";

    clear;

    echo "Running: "$(basename ${source});
    echo "--- Example source ---";
    cat -n ${source};
    echo -e "\n=====";

    echo "--- Expected output ---";
    cat ${output};
    echo "--- Executed output ---";
    (
      cd "${dir}";
      if [ -f ${input} ]; then
        timeout 5 ${cmd} ${source} < ${input} 1>${result} 2>&1;
      else
        timeout 5 ${cmd} ${source} 1>${result} 2>&1;
      fi

      [ $? -eq 124 ] && echo "*** TIMED OUT ***";
      cat "${result}";
    );

    echo "--- Veredict ---";
    cmp ${output} ${dir}/${result} 1>/dev/null 2>&1 && echo "OKAY" || echo "MISMATCH";
    # rm "${dir}/${result}";
    echo "=====";
    read y;
done

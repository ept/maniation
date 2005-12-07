function retval = bonebases(b)

    boneaxis = [0;1;0];

    base=b(1,1:3)

    for i=1:(rows(b)-1)
        v = b(i+1, 1:3)' - b(i, 1:3)';
        len = sqrt(sumsq(v));
        base = len*boneaxis'
    endfor
    
endfunction

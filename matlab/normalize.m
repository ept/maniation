function retval = normalize(vector)
    % Normalize a vector or quaternion to have unit length (magnitude).
    retval = vector / sumsq(vector);
endfunction

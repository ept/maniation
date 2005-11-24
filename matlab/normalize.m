function retval = normalize(vector)
    % Normalize a vector or quaternion to have unit length (magnitude).
    retval = vector / sqrt(sumsq(vector));
endfunction

function retval = isquaternion(q)
    retval = (nargin == 1) & isvector(q) & (columns(q) == 1) & (rows(q) == 4);
endfunction

function retval = anyortho(vec)
    % Returns some vector which is orthogonal to the vector provided.
    % No other guarantees are made. If the input vector is null, the
    % output is also null.

    retval = vec;
    magsq = sumsq(vec);
    if (magsq > 0.0000000000001)
        vec = abs(vec/sqrt(magsq));
        [v1,i1] = max(vec);
        vec(i1) = 0;
        [v2,i2] = max(vec);
        if ((i2 < 1) || (i1 == i2))
            i2 = 1;
            if (i1 == 1) i2 = 2; endif
        endif
        mask = zeros(rows(vec), columns(vec));
        mask(i1) = 1;
        mask(i2) = 1;
        retval(not(mask)) = 0;
        v2 = retval(i2);
        retval(i2) = -retval(i1);
        retval(i1) = v2;
    endif
endfunction

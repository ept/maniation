function retval = plotblender(result)
    % Write the results of a simulation to a file called `blenderdata'
    % which can be imported into blender.
    data = zeros(columns(result), 7*rows(result)/13);
    for n=1:rows(result)/13
        data(:, 7*n-6:7*n) = result'(:, 13*n-12:13*n-6);
    endfor
    save -ascii blenderdata data
endfunction

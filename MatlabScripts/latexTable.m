function latexTable()
%Create the code in latex for a table with the satisfaction achieved
%by the three approaches

cd('../General');
% Initialization
clear ; close all; clc
X = load('satisfaction.txt');
% statarray = grpstats(ds,groupvar) returns a dataset array with the means 
% for the data groups in the dataset array ds determined by the values of 
% the grouping variable or variables specified in groupvar.
A = grpstats(X, [X(:,1)]);
xVal = [mean(reshape(A(1,3:end),50,12),1);
     mean(reshape(A(2,3:end),50,12),1);
     mean(reshape(A(3,3:end),50,12),1)];
data = [ xVal(2,:); xVal(1,:) ; xVal(3,:)];
rowLabels = (50:50:600);
columnLabels = {'Random', 'close', 'GA'};
cd('../Functions');
matrix2latex(data', '../General/out.tex', 'rowLabels', rowLabels, 'columnLabels', ...
    columnLabels, 'alignment', 'c', 'format', '%-6.2f');
end


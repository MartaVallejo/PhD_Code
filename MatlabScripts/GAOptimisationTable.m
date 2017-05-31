function GAOptimisationTable()
%% GA Optimisation Data from different scenarios

cd('../General');
% Initialization
clear ; close all; clc
fileID=fopen('GA_DATA.txt');
X = textscan(fileID,'%s %d %d %d %d %d %d %d %d %f %f %d %s %d %d %d %s %d %s %s %s %d %d %d');
fclose(fileID);

%Create an empty table
t = uitable; 
data = [X{6}'; X{7}'; X{8}'; X{9}'; X{10}'; X{11}'];

set(t,'Data', data');
set(t,'Position',[4 191 700 90]);
set(t,'ColumnName',{'Generations', 'Mutations', 'Worst', 'Best', 'Mean', 'SD'});
set(t,'Columnformat',{'bank', 'bank', 'bank', 'bank', 'bank', 'bank'});
set(t,'ColumnWidth',{110});
set(t,'RowName',[1:1:size(data,2)]);
set(t,'FontSize', 9);
saveas(t, ['tab1', 'eps'],'epsc');
cd('../Functions');
end


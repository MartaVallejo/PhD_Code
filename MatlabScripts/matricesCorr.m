function matricesCorr()
%% Study the correlation of two matrices

cd('../General');
% Initialization
clear ; close all; clc
A = importdata('realFitness.txt');
B = importdata('Density.txt');

AA = A(:,2:end);
BB = B(:,2:end);

% As a big sample
[R,P,RLO,RUP] = corrcoef(AA(:), BB(:));

disp('matrix R of correlation coefficients')
R

disp('matrix of p-values for testing the hypothesis of no correlation')
%  Each p-value is the probability of getting a correlation as large as the observed value by random chance when the true correlation is zero. 
% If P(i,j) is small, say less than 0.05, then the correlation R(i,j) is significant.
P

disp('lower bounds for a 95% confidence interval for each coefficient')
RLO

disp('upper bounds for a 95% confidence interval for each coefficient')
RUP

cd('../Functions');
end

